#!/usr/bin/env python

import logging
import requests
import threading

from escpos.printer import Usb
from datetime import datetime

import time
import os

import constantes

import sqlite3
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(22, GPIO.OUT) # Speaker
GPIO.setup(5, GPIO.OUT) # foco rojo
GPIO.setup(6, GPIO.OUT) # foco verde
GPIO.setup(13, GPIO.OUT) # foco amarillo

GPIO.setup(12, GPIO.IN, pull_up_down=GPIO.PUD_UP) # btn 30 mins
GPIO.setup(25, GPIO.IN, pull_up_down=GPIO.PUD_UP) # btn 45 mins
GPIO.setup(24, GPIO.IN, pull_up_down=GPIO.PUD_UP) # btn 60 mins

GPIO.setup(16, GPIO.IN, pull_up_down=GPIO.PUD_UP) # btn Rechazar
GPIO.setup(26, GPIO.IN, pull_up_down=GPIO.PUD_UP) # btn Reimprimir

from apscheduler.schedulers.background import BackgroundScheduler


def puedeAceptarPedidos():
	start_time = int(10)*60 + int(00)
	end_time = int(23)*60 + int(00)
	current_time =  datetime.now().hour*60 +datetime.now().minute
	return start_time <= current_time and end_time >= current_time

def solicitarNuevoPedido(conn, cursor):
	endpoint = constantes.LOLA_ENDPOINT + 'pizzahut/pendiente'

        headers = constantes.LOLA_HEADER

	resp = requests.get(endpoint, headers = headers)
	if resp.status_code != 200:
		# This means something went wrong.
		logging.error('No se pudo obtener pedidos desde lola Status: %s' % resp.status_code)
		raise ApiError('GET /pendiente/ {}'.format(resp.status_code))

	datosPedido = resp.json()

	nuevosPedidos = len(datosPedido)

	if nuevosPedidos > 0:
		rows = [(pedido['id'], pedido['fecha'], pedido['textoPedido'], 0) for pedido in datosPedido]
	
		try:
			cursor.executemany('insert into pedido_pizzahut values (?,?,?,?)', rows)
			conn.commit()

			if nuevosPedidos > 1:
				prenderFocoAmarillo()
			
			prenderFocoVerde()
			imprimirPedido(datosPedido[0]['textoPedido']) 
			emitirSonido(5)
			logging.info ('Se registraron %s pedidos' % nuevosPedidos)

		except Exception as e:
			logging.info ('Se hace rollback %s' % e)
			conn.rollback()

	else:
		#print('No hay pedidos')
		logging.info('No hay pedidos por recibir')


def existePedidosSinProcesar(cursor):
	buscarPedidosSinProcesar(cursor , 'LIMIT 2')
	return cursor.fetchall()


def obtenerPrimerPedidoSinProcesar(cursor):
	buscarPedidosSinProcesar(cursor, 'LIMIT 1')
	return cursor.fetchone()

def buscarPedidosSinProcesar(cursor, limite):
	sql = ("SELECT * FROM pedido_pizzahut WHERE tiempo_entrega = 0 order by id %s" % limite)
        return cursor.execute(sql)


def iniciarThreadPedidos():
	t = threading.Thread(target=buscarUltimoPedido)
	t.start()

def buscarUltimoPedido():

	if puedeAceptarPedidos():
		print 'Si acepta pedidos'

		conn = sqlite3.connect(constantes.LOLA_DB)
		conn.row_factory = sqlite3.Row
		cursor = conn.cursor()

		pedidosSinProcesar = existePedidosSinProcesar(cursor)
		numeroPedidosSinProcesar = len(pedidosSinProcesar)
		print('Numero pedidos sin procesar: %s' % numeroPedidosSinProcesar )
 
		if numeroPedidosSinProcesar == 0:
			solicitarNuevoPedido(conn, cursor)

		manejarFocos(numeroPedidosSinProcesar)

		cursor.close()
		conn.close()

	else:
		logging.warning ('No se aceptan pedidos en este momento')


def confirmarPedidoLola(idPedido, tiempoEntrega):
	endpoint = constantes.LOLA_ENDPOINT + ('/pizzahut/confirmar/%d/%d' % (idPedido, tiempoEntrega))

	headers = constantes.LOLA_HEADER

	resp = requests.post(endpoint, headers = headers)
	if resp.status_code != 200:
		# This means something went wrong.
		raise ApiError('GET /confirmar/ {}'.format(resp.status_code))

	print('Confirmacion: %s' % resp)


def confirmarPedidoLocal(conn, cursor, idPedido, tiempoEntrega):
	sql = "UPDATE pedido_pizzahut set tiempo_entrega = ? where id = ?"
	cursor.execute(sql,(tiempoEntrega, idPedido))
	conn.commit()


def confirmarPedido(tiempoEntrega):

	conn = sqlite3.connect(constantes.LOLA_DB)
	conn.row_factory = sqlite3.Row
	cursor = conn.cursor()

	pedidosSinProcesar = obtenerPrimerPedidoSinProcesar(cursor)

	if pedidosSinProcesar:
		idPedido = pedidosSinProcesar['id']
		confirmarPedidoLola(idPedido, tiempoEntrega)
		confirmarPedidoLocal(conn, cursor, idPedido, tiempoEntrega)

		apagarFocoVerde()

	else:
		print('No hay nada por confirmar')

	emitirSonido(1)
	time.sleep(5)
	procesarSiguientePedido(cursor)

	cursor.close()
	conn.close()


def procesarSiguientePedido(cursor):
	pedidosSinProcesar = existePedidosSinProcesar(cursor)
	numeroPedidosSinProcesar = len(pedidosSinProcesar)
	manejarFocos(numeroPedidosSinProcesar)

	if numeroPedidosSinProcesar > 0:
		emitirSonido(5)
		imprimirPedido(pedidosSinProcesar[0]['descripcion'])

def reimprimirPedido():
	print('Reimprimiendo pedido')
	conn = sqlite3.connect(constantes.LOLA_DB)
	conn.row_factory = sqlite3.Row
	cursor = conn.cursor()

	pedidosSinProcesar = obtenerPrimerPedidoSinProcesar(cursor)

	if pedidosSinProcesar:
		imprimirPedido(pedidosSinProcesar['descripcion'])

	cursor.close()
	conn.close()
	time.sleep(5)


def imprimirPedido(textoPedido):
	try:
		Epson.text(textoPedido)
		Epson.cut()

	except Exception as e:
		emitirSonido(3)
		logging.info ('Impresora Apagada o no conectada %s' % e)
		raise ValueError('Impresora Apagada o no conectada')


def inicializarImpresora():
	try:
		global Epson
		Epson = Usb(0x04b8,0x0e15)
		emitirSonido(1)

	except Exception as e:
		emitirSonido(3)
		logging.info ('Impresora Apagada o no conectada %s' % e)
		raise ValueError('Impresora Apagada o no conectada')


def emitirSonido(numeroVeces):
	for i in range(0, numeroVeces, 1):
		speaker = GPIO.PWM(22, 500)
		speaker.start(50)
		time.sleep(0.25)
		speaker.stop()
		time.sleep(0.25)

def manejarFocos(numeroPedidosSinProcesar):
	if numeroPedidosSinProcesar == 0:
		apagarFocos()

	elif numeroPedidosSinProcesar == 1:
		apagarFocoAmarillo()
		prenderFocoVerde()

	elif numeroPedidosSinProcesar > 1:
		prenderFocoAmarillo()
		prenderFocoVerde()


def apagarFocos():
	apagarFocoAmarillo()
	apagarFocoVerde()

def apagarFocoVerde():
	# Apaga foco verde
	GPIO.output(6, False)

def apagarFocoAmarillo():
	# Apaga foco amarillo
	GPIO.output(13, False)

def prenderFocoVerde():
	GPIO.output(6, True)

def prenderFocoAmarillo():
	GPIO.output(13, True)

if __name__ == '__main__':
	constantes.constantesLola()
	logging.basicConfig(filename=constantes.LOLA_LOG, level=logging.INFO)
	inicializarImpresora()
	apagarFocos()

	scheduler = BackgroundScheduler()
	job = scheduler.add_job(iniciarThreadPedidos, 'cron', hour='10-23', second='*/20', id='lola_job', misfire_grace_time=60)
	scheduler.start()
	print('Press Ctrl+{0} to exit'.format('Break' if os.name == 'nt' else 'C'))

    	try:
        	# This is here to simulate application activity (which keeps the main thread alive).
        	while True:
			btn30MinPresionado = not GPIO.input(12)
			btn45MinPresionado = not GPIO.input(25)
			btn60MinPresionado = not GPIO.input(24)

			btnRechazarPresionado = not GPIO.input(16)
			btnReimprimirPresionado = GPIO.input(26)

			if btn30MinPresionado:
				confirmarPedido(30)

			elif btn45MinPresionado:
				confirmarPedido(45)

			elif btn60MinPresionado:
				confirmarPedido(60)

			elif btnRechazarPresionado:
				confirmarPedido(-1)

			elif btnReimprimirPresionado:
				reimprimirPedido()


	except (KeyboardInterrupt, SystemExit):
		job.remove();
		scheduler.shutdown()
		GPIO.cleanup()



