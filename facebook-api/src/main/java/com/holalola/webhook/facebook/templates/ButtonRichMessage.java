package com.holalola.webhook.facebook.templates;

import java.util.List;

import com.holalola.webhook.facebook.payload.ButtonTemplatePayload;

public class ButtonRichMessage extends RichMessage {

	public ButtonRichMessage(String speech, List<ButtonGeneral> buttons) {
		super(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload(speech, buttons)));
	}
}
