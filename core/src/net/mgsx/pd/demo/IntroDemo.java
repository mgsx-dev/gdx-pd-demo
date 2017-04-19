package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class IntroDemo implements Demo {

	@Override
	public Actor create(Skin skin) 
	{
		Table list = new Table(skin);
		
		float pad = 30;
		
		String info = "GDX-PD\n" +
				"- Pure Data extension for LibGDX -";
		
		Label infoLabel = new Label(info, skin, "title");
		infoLabel.setAlignment(Align.center);
		list.add(infoLabel).row();
		
		list.add("This demo illustrates procedural audio in video games.").padTop(pad).row();
		list.add("Design is volontary simple to help understanding.").row();
		list.add("So, present examples might sounds cheap.").row();
		
		
		list.add("More information on the demo :").padTop(pad).row();
		list.add(link("GDX-PD-DEMO on GitHub", "https://github.com/mgsx-dev/gdx-pd-demo#help", skin)).row();
		
		
		list.add("More information on GDX-PD :").padTop(pad).row();
		list.add(link("GDX-PD on GitHub", "https://github.com/mgsx-dev/gdx-pd", skin)).row();
		return list;
	}

	private Actor link(String label, final String url, Skin skin) 
	{
		TextButton button = new TextButton(label, skin);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.net.openURI(url);
			}
		});
		return button;
	}

	@Override
	public void dispose() {
	}
	
	@Override
	public String toString() {
		return "< select a demo >";
	}

}
