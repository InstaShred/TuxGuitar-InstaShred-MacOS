package org.herac.tuxguitar.InstaShred;

import org.herac.tuxguitar.editor.event.TGRedrawEvent;
import org.herac.tuxguitar.event.TGEventManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class InstaShredPlugin implements TGPlugin {

	private ExtendedTGEventListener listener;
	
	@Override
	public String getModuleId() {
		return "tuxguitar-instashred";
	}

	@Override
	public void connect(TGContext context) throws TGPluginException {
		
		System.out.println("Connecting tuxguitar-instashred");
		
		if (this.listener == null) {
			this.listener = new RedrawListener(context);
			// addListener arguments is the event to fire on (TGRedrawEvent.EVENT_TYPE == "ui-redraw")
			TGEventManager.getInstance(context).addListener(TGRedrawEvent.EVENT_TYPE, this.listener);
		}
	}

	@Override
	public void disconnect(TGContext context) throws TGPluginException {
		if( this.listener != null ) {
			this.listener.disconnect();
			System.out.println("Disconnecting tuxguitar-instashred");
			
			TGEventManager.getInstance(context).removeListener(TGRedrawEvent.EVENT_TYPE, this.listener);	
			this.listener = null;
		}
	}
}
