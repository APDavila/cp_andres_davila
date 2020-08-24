#!/usr/bin/env python

import logging
import requests
import threading

from escpos.printer import Usb
from datetime import datetime

import time
import os

import constantes


from apscheduler.schedulers.background import BackgroundScheduler
from playsound import playsound


def puedeAceptarPedidos():
	start_time = int(10)*60 + int(00)
	end_time = int(23)*60 + int(00)
	current_time =  datetime.now().hour*60 +datetime.now().minute
	return start_time <= current_time and end_time >= current_time

def solicitarNuevoPedido():
	endpoint = constantes.LOLA_ENDPOINT + 'pizzahut/pendiente'

	headers = constantes.LOLA_HEADER

	resp = requests.get(endpoint, headers = headers)
	if resp.status_code != 200:
		# This means something went wrong.
		logging.error('No se pudo obtener pedidos desde lola Status: %s' % resp.status_code)
		return None

	datosPedido = resp.json()

	nuevosPedidos = len(datosPedido)

	if nuevosPedidos > 0:
			imprimirPedido(datosPedido[0]['textoPedido']) 
			emitirSonido('nuevo_pedido.wav')
			# ----------------> Aqui crear un metodo para saber que ya se imprimio y no se siga imprimiendo N veces hasta que confirmen el pedido
			logging.info ('Se imprimio el pedido No. %s' % datosPedido[0]['id'])

	else:
		logging.info('No hay pedidos por recibir')


def iniciarThreadPedidos():
	t = threading.Thread(target=buscarUltimoPedido)
	t.start()

def buscarUltimoPedido():

	if puedeAceptarPedidos():
		solicitarNuevoPedido()

	else:
		logging.warning ('No se aceptan pedidos en este momento')


def reimprimirPedido():
	# Hacer metodo para obtener el ultimo pedido reimpreso
	if pedidosSinProcesar:
		imprimirPedido(pedidosSinProcesar['descripcion'])


def imprimirPedido(textoPedido):
	try:
		# Dar formato al pedido
		Epson.text(textoPedido)
		Epson.cut()

	except Exception as e:
		emitirSonido('verificar_impresora.wav')
		logging.info ('Impresora Apagada o no conectada %s' % e)
		return None


def inicializarImpresora():
	try:
		global Epson
		Epson = Usb(0x04b8,0x0e15)

	except Exception as e:
		emitirSonido('verificar_impresora.wav')
		logging.info ('Impresora Apagada o no conectada %s' % e)
		time.sleep(10)
		inicializarImpresora()
		return None


def emitirSonido(archivo):
	#playsound('c:\holalola\%s' % archivo)
	return None


if __name__ == '__main__':
	constantes.constantesLola()
	logging.basicConfig(filename=constantes.LOLA_LOG, level=logging.INFO)
	inicializarImpresora()

	scheduler = BackgroundScheduler()
	job = scheduler.add_job(iniciarThreadPedidos, 'cron', hour='10-23', second='*/20', id='lola_job', misfire_grace_time=60)
	scheduler.start()
	print('Press Ctrl+{0} to exit'.format('Break' if os.name == 'nt' else 'C'))

	try:
		# This is here to simulate application activity (which keeps the main thread alive).
		while True:
			print('Idle')
			time.sleep(20)


	except (KeyboardInterrupt, SystemExit):
		job.remove();
		scheduler.shutdown()



