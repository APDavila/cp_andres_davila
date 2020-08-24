

def constantesLola():

	global LOLA_ENDPOINT
	global LOLA_HEADER
	global LOLA_DB
	global LOLA_PATH
	global LOLA_LOG

	
	LOLA_ENDPOINT = 'http://ec2-54-80-4-29.compute-1.amazonaws.com:8080/webhook/resources/'
	#LOLA_ENDPOINT = 'https://a67cde2c.ngrok.io/webhook/resources/'

	LOLA_HEADER = {'lolaAuth' : 'sWXL3hl6uaqgUezmmAUoPhjit8lZvHmXReuyeIOvKUxiJsWVuhWNbcJSzUc1tNfY6XNeJrjKLquu9mvTiKSV09k8tCYglFEJ9NfTCajv0ItVUO9pumFaC6bTu4oBw9IXoki22laCBEg6AuvJ8DoLEyUtmznD8iSLsbeakLi0K074nRbOq7f71ebFmeFTDPXsDiWzDcgrJCnhkgk6juBETYLjnBgxLc9lO8YoZcCWDcMOgD1xhCmSpNrzRjS1fiHItuSpElBKkNpFJSrabaLtJ98gkNAZGxbyGgErmnW4CqFv'}


	LOLA_PATH = 'c:\holalola'
	LOLA_DB = LOLA_PATH + '/db/holalola'
	LOLA_LOG = LOLA_PATH + '\log\pedidos.log'

