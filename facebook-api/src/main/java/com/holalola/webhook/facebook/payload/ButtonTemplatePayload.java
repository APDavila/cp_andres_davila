package com.holalola.webhook.facebook.payload;

import java.util.List;

import com.holalola.webhook.facebook.templates.ButtonGeneral;

public class ButtonTemplatePayload extends PayloadGeneral {
	
	private final String template_type = "button";
	private String text;
	private List<ButtonGeneral> buttons;
	
	public ButtonTemplatePayload(String text, List<ButtonGeneral> buttons) {
		this.text = text;
		this.buttons = buttons;
	}

	public String getTemplate_type() {
		return template_type;
	}

	public String getText() {
		return text;
	}

	public List<ButtonGeneral> getButtons() {
		return buttons;
	}
}
