#!/usr/bin/python

import logging
import requests

from datetime import datetime
import time
import os

import RPi.GPIO as GPIO

import constantes

GPIO.setmode(GPIO.BCM)
GPIO.setup(23, GPIO.OUT) # foco
GPIO.setup(25, GPIO.OUT) # motor

from apscheduler.schedulers.background import BackgroundScheduler


def ejecutarConsulta(accion):
	endpoint = constantes.LOLA_ENDPOINT + ('domotica/%s' % accion)

	headers = constantes.LOLA_HEADER

	resp = requests.get(endpoint, headers = headers)
	if resp.status_code != 200:
		# This means something went wrong.
		logging.error('No se pudo obtener acciones domotica desde lola Status: %s' % resp.status_code)
		raise ApiError('GET /domotica/%s Status %d' % (accion, resp.status_code))

	return resp.json()


def buscarAccionDomotica():

	domotica = ejecutarConsulta('pendiente')
	id = domotica['id']

	if id > 0:
		accion = False
		objeto = 0

		if domotica['accion'] == 'ENCENDER':
			accion = True

		if domotica['objeto'] == 'FOCO':
			objeto = 23
		
		elif domotica['objeto'] == 'MOTOR':
			objeto = 25

		if objeto > 0:
			# Prende foco
			GPIO.output(objeto, accion)
			return True
		

	else:
		#print('No hay nada para domotica')
		logging.info('No hay nada para domotica')

	return False

def confirmarDomotica():
	ejecutarConsulta('confirmar')

def ejecutarDomotica():
	if buscarAccionDomotica():
		confirmarDomotica()
	

if __name__ == '__main__':
	constantes.constantesLola()
	logging.basicConfig(filename='./log/domotica.log', level=logging.INFO)

	scheduler = BackgroundScheduler()
	scheduler.add_job(ejecutarDomotica, 'interval', seconds=4)
	scheduler.start()
	
	print('Press Ctrl+{0} to exit'.format('Break' if os.name == 'nt' else 'C'))

    	try:
        	# This is here to simulate application activity (which keeps the main thread alive).
        	while True:
            		time.sleep(200)
	except (KeyboardInterrupt, SystemExit):
        	# Not strictly necessary if daemonic mode is enabled but should be done if possible
		scheduler.shutdown()



