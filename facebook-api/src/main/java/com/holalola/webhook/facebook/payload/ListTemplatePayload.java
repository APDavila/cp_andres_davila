package com.holalola.webhook.facebook.payload;

import java.util.Arrays;
import java.util.List;

import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.Element;

public class ListTemplatePayload extends PayloadGeneral {

	private static final String CON_CABECERA = "large";
	private static final String SIN_CABECERA = "compact";

	private final String template_type = "list";
	private String top_element_style;
	private List<Element> elements; // min 2, max 4
	private List<ButtonGeneral> buttons; // solo 1

	public ListTemplatePayload(boolean conCabecera, List<Element> elements, ButtonGeneral button) {
		this.top_element_style = conCabecera ? CON_CABECERA : SIN_CABECERA;
		this.elements = elements;
		buttons = button == null ? null : Arrays.asList(button);
	}

	public String getTemplate_type() {
		return template_type;
	}

	public String getTop_element_style() {
		return top_element_style;
	}

	public List<Element> getElements() {
		return elements;
	}

	public List<ButtonGeneral> getButtons() {
		return buttons;
	}

}
