import requests

from datetime import datetime
import time
import os

import constantes

import sqlite3
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(23, GPIO.OUT)
GPIO.setup(24, GPIO.IN, pull_up_down=GPIO.PUD_UP)


from apscheduler.schedulers.background import BackgroundScheduler


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


def obtenerPedidoSinProcesar(cursor):
	sql = "SELECT * FROM pedido_pizzahut WHERE tiempo_entrega = 0"
	cursor.execute(sql)
	return cursor.fetchone()


def confirmarPedido(tiempoEntrega):

	conn = sqlite3.connect("./db/holalola")
	cursor = conn.cursor()
 
	pedidoSinProcesar = obtenerPedidoSinProcesar(cursor)

	if pedidoSinProcesar:
		idPedido = pedidoSinProcesar[0]
		confirmarPedidoLola(idPedido, tiempoEntrega)
		confirmarPedidoLocal(conn, cursor, idPedido, tiempoEntrega)

		# Apagar el foco de pedido pendiente
		time.sleep(0.5)
		GPIO.output(23, False)

	else:
		print('No hay nada por confirmar')

	cursor.close()
	conn.close()



if __name__ == '__main__':
	
	print('Press Ctrl+{0} to exit'.format('Break' if os.name == 'nt' else 'C'))
	constantes.constantesLola()

    	try:
		while True:
			btn45MinPresionado = not GPIO.input(24)
			if btn45MinPresionado:
				confirmarPedido(45)

	except (KeyboardInterrupt, SystemExit):
		print 'Usuario finaliza programa'
