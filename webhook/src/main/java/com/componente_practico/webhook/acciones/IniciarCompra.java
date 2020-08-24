package com.componente_practico.webhook.acciones;

import static com.componente_practico.util.ApiAiUtil.obtenerValorParametro;
import static com.componente_practico.util.ApiAiUtil.obtenerValorParametroSCV2;
import static com.componente_practico.util.ApiAiUtil.obtenerValorParametroV2;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.util.UrlUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.ejb.dao.RespuestaDao;
import com.holalola.webhook.ejb.modelo.Respuesta;
import com.holalola.webhook.enumeracion.Categoria;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.ButtonRichMessageV2;
import com.holalola.webhook.facebook.templates.WebUrlButton;

import ai.api.model.Result;

@Stateless
public class IniciarCompra {
	
	private static final String PARAM_ID_PROVEEDOR = "idProveedor";
	public static final String CONTEXTO_PEDIR_COMIDA = "pedir_comida";
	private static final String PARAM_CANTIDAD = "cantidad.original";
	
	@EJB
	RespuestaDao respuestaDao;
	
	@EJB
	ProveedorDao proveedorDao;
	
	public ResponseMessageApiAiV2 verMenuProveedor(QueryResult resultAi, Usuario usuario) {
		final String idProveedor = obtenerValorParametroSCV2(resultAi, PARAM_ID_PROVEEDOR, "");
		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.VER_PERFIL);
		Proveedor proveedorActual = proveedorDao.obtenerPorId(Long.parseLong(idProveedor));
		String speech = String.format("Listo %s, presiona Ver Menú para ver las opciones que tenemos para ti.!!!",
				usuario.getNombreFacebook());
		//UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
		List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver Menú",
				UrlUtil.armarUrlIniciarCompra(usuario.getId(),idProveedor,"home",proveedorActual .getServicio()), true)); 
		PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));
//		UrlUtil.armarUrlIniciarCompra((usuario.getId()));

		return new ResponseMessageApiAiV2(speech, "", data, null);
	}
}
