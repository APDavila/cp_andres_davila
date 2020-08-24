package com.holalola.webhook.facebook.templates;

import java.util.List;

import com.holalola.webhook.facebook.payload.ButtonTemplatePayload;

public class ButtonRichMessageV2 extends RichMessageV2{
	public ButtonRichMessageV2(String speech, List<ButtonGeneral> buttons) {
		super(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload(speech, buttons)));
	}
}
