import logging
import requests

from datetime import datetime
import time
import os

import constantes

import sqlite3
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(16, GPIO.OUT) # Speaker
GPIO.setup(23, GPIO.OUT) # foco

from apscheduler.schedulers.background import BackgroundScheduler


def puedeAceptarPedidos():
	start_time = int(10)*60 + int(00)
	end_time = int(22)*60 + int(00)
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
	print(resp.json())

	id = datosPedido['id']

	if id > 0:
		try:
			textoPedido = datosPedido['textoPedido']
			fecha = datosPedido['fecha']

			row = [(id, fecha, textoPedido, 0)]
			cursor.executemany('insert into pedido_pizzahut values (?,?,?,?)', row)
			conn.commit()
			
			# Prende foco
			GPIO.output(23, True)
			emitirSonido() 
			logging.info ('Se registro pedido con id %s' % id)

		except Exception as e:
			logging.info ('Se hace rollback %s' % e)
			conn.rollback()

	else:
		#print('No hay pedidos')
		logging.info('No hay pedidos por recibir')


def existePedidoSinProcesar(cursor):
	sql = "SELECT * FROM pedido_pizzahut WHERE tiempo_entrega = 0"
	cursor.execute(sql)
	return cursor.fetchall()


def buscarUltimoPedido():

	if puedeAceptarPedidos():
		print 'Si acepta pedidos'

		conn = sqlite3.connect("./db/holalola")
		cursor = conn.cursor()
 
		if not existePedidoSinProcesar(cursor):
			solicitarNuevoPedido(conn, cursor)
		else:
			# Prende foco
			GPIO.output(23, True)
			#print('Existe un pedido pendiente sin atender')
			logging.info('Existe un pedido sin atender')

		cursor.close()
		conn.close()

	else:
		logging.warning ('No se aceptan pedidos en este momento')

def emitirSonido():
	for i in range(0, 5, 1):
		speaker = GPIO.PWM(16, 500)
		speaker.start(50)
		time.sleep(0.25)
		speaker.stop()
		time.sleep(0.25)



if __name__ == '__main__':
	constantes.constantesLola()
	logging.basicConfig(filename='./log/ver_pedidos.log', level=logging.INFO)

	scheduler = BackgroundScheduler()
	scheduler.add_job(buscarUltimoPedido, 'interval', seconds=10)
	scheduler.start()
	#buscarUltimoPedido()
	print('Press Ctrl+{0} to exit'.format('Break' if os.name == 'nt' else 'C'))

    	try:
        	# This is here to simulate application activity (which keeps the main thread alive).
        	while True:
            		time.sleep(200)
	except (KeyboardInterrupt, SystemExit):
        	# Not strictly necessary if daemonic mode is enabled but should be done if possible
		scheduler.shutdown()



