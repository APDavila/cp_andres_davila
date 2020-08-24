#!flask/bin/python
import RPi.GPIO as GPIO
import os

from flask import Flask, jsonify, abort, request, make_response, url_for
#from flask.ext.httpauth import HTTPBasicAuth

GPIO.setmode(GPIO.BCM)
GPIO.setup(23, GPIO.OUT)

app = Flask(__name__, static_url_path = "")

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify( { 'error': 'Not found' } ), 404)

@app.route('/lola/accion/<accion>', methods = ['GET'])
def ejecutar_accion(accion):
	if accion == 'ENCENDER':
		print('Prender foco')
		GPIO.output(23, True)
	else:
		print('Apagar foco')
		GPIO.output(23, False)
	
	return jsonify( { 'result': True } )

if __name__ == '__main__':
     app.run()
