function validarIdentificacion(tipoDocumento, identificacion) {

	/*
	 * Tipos de Documentos:
	 * 
	 * R --> RUC C --> Cedula P --> Pasaporte
	 */
	
	if (tipoDocumento == '' || identificacion == '') return false;

	if (tipoDocumento != 'P') {
		
		if (identificacion.length != 10 && identificacion.length != 13) return false;

		var lv_cedula;
		var lv_vec3;
		var lv_numidentificacion;
		var ll_rc;
		var ll_fin;

		ll_rc = false;
		;
		lv_numidentificacion = identificacion;
		// Control de los 3 últimos dígitos
		ll_fin = identificacion.substring(10);

		if (ll_fin != '001' && tipoDocumento.toUpperCase() == 'R')
			return false;
		if (isNaN(Number(identificacion)))
			return false;

		lv_cedula = identificacion.substring(0, 10);
		if (isNaN(Number(lv_cedula))
				|| (lv_numidentificacion.length != 13 && tipoDocumento == 'R'))
			return false;
		if (isNaN(Number(lv_cedula))
				|| (lv_numidentificacion.length != 10 && tipoDocumento == 'C'))
			return false;

		lv_vec3 = Number(lv_numidentificacion.substring(2, 3));
		// alert(lv_vec3);

		if ((lv_vec3 >= 0 && lv_vec3 <= 5) || tipoDocumento == 'C')
			ll_rc = validaCedula(lv_cedula);
		else if (lv_vec3 == 6)
			ll_rc = validaTercero6(lv_cedula);
		else if (lv_vec3 == 9)
			ll_rc = validaTercero9(lv_cedula);

		return ll_rc;

	} else {
		if (identificacion.length > 0)
			return true;
	}

	return false;
}

function validaCedula(cedula) {
	// Control de provincia entre 1 y 24
	lv_prov = Number(cedula.substring(0, 2));

	if (lv_prov >= 1 && lv_prov <= 24) {
		lv_numced = cedula;
		ll_TenDig = Number(cedula.substring(9));
		ll_sum = 0;

		ll_Cnt = 0;
		ll_CntPos = 0;
		while (ll_CntPos < 9) {
			ll_CntPos = 2 * ll_Cnt + 1;
			lv_StrNum = lv_numced.substring(ll_CntPos - 1, ll_CntPos);
			ll_multi = Number(lv_StrNum) * 2;
			if (ll_multi >= 10)
				ll_multi = 1 + (ll_multi % 10);
			ll_sum += ll_multi;
			ll_Cnt += 1;
		}

		ll_Cnt = 1;
		ll_CntPos = 1;
		while (ll_CntPos < 8) {
			ll_CntPos = 2 * ll_Cnt;
			lv_StrNum = lv_numced.substring(ll_CntPos - 1, ll_CntPos);
			ll_sum += Number(lv_StrNum);
			ll_Cnt += 1;
		}

		ll_cociente = Math.floor(ll_sum / 10);
		ll_decena = (ll_cociente + 1) * 10;
		ll_verificador = ll_decena - ll_sum;

		if (ll_verificador == 10)
			ll_verificador = 0;
		if (ll_verificador == ll_TenDig)
			return true;
		else
			return false;
	} else {
		return false;
	}

}

function validaTercero6(identificacionSeis) {
	lv_identificacion_seis = identificacionSeis;

	ll_Uno = Number(lv_identificacion_seis.substring(0, 1));
	ll_Dos = Number(lv_identificacion_seis.substring(1, 2));
	ll_Tre = Number(lv_identificacion_seis.substring(2, 3));
	ll_Cua = Number(lv_identificacion_seis.substring(3, 4));
	ll_Cin = Number(lv_identificacion_seis.substring(4, 5));
	ll_Sei = Number(lv_identificacion_seis.substring(5, 6));
	ll_Sie = Number(lv_identificacion_seis.substring(6, 7));
	ll_Och = Number(lv_identificacion_seis.substring(7, 8));
	ll_Nue = Number(lv_identificacion_seis.substring(8, 9));

	ll_sum = ll_Uno * 3 + ll_Dos * 2 + ll_Tre * 7 + ll_Cua * 6 + ll_Cin * 5
			+ ll_Sei * 4 + ll_Sie * 3 + ll_Och * 2;

	while (ll_sum > 11) {
		ll_sum -= 11;
	}
	ll_dverr2 = 11 - ll_sum;
	if (ll_dverr2 == 10) {
		ll_dverr2 = 0;
		return false;
	} else {
		if (ll_dverr2 != ll_Nue)
			return false;
		else
			return true;
	}
}

function validaTercero9(identificacionNueve) {
	// alert("Tercero 9");
	lv_identificacion_nueve = identificacionNueve;

	ll_Uno = Number(lv_identificacion_nueve.substring(0, 1));
	ll_Dos = Number(lv_identificacion_nueve.substring(1, 2));
	ll_Tre = Number(lv_identificacion_nueve.substring(2, 3));
	ll_Cua = Number(lv_identificacion_nueve.substring(3, 4));
	ll_Cin = Number(lv_identificacion_nueve.substring(4, 5));
	ll_Sei = Number(lv_identificacion_nueve.substring(5, 6));
	ll_Sie = Number(lv_identificacion_nueve.substring(6, 7));
	ll_Och = Number(lv_identificacion_nueve.substring(7, 8));
	ll_Nue = Number(lv_identificacion_nueve.substring(8, 9));
	ll_Die = Number(lv_identificacion_nueve.substring(9));

	ll_sum = ll_Uno * 4 + ll_Dos * 3 + ll_Tre * 2 + ll_Cua * 7 + ll_Cin * 6
			+ ll_Sei * 5 + ll_Sie * 4 + ll_Och * 3 + ll_Nue * 2;

	while (ll_sum > 11) {
		ll_sum -= 11;
	}

	ll_dverr2 = 11 - ll_sum;

	if (ll_dverr2 == 10) {
		ll_dverr2 = 0;
		return false;
	} else {
		if (ll_dverr2 != ll_Die)
			return false;
		else
			return true;
	}
}