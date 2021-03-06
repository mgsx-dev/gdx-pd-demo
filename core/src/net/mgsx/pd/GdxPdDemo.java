package net.mgsx.pd;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.mgsx.pd.demo.AtmosphereDemo;
import net.mgsx.pd.demo.Demo;
import net.mgsx.pd.demo.IntroDemo;
import net.mgsx.pd.demo.MicAnalysisDemo;
import net.mgsx.pd.demo.MicProcessingDemo;
import net.mgsx.pd.demo.MidiMusicDemo;
import net.mgsx.pd.demo.SoundEffectsDemo;

public class GdxPdDemo extends ApplicationAdapter 
{
	public static PdConfiguration config;
	
	private Stage stage;
	private Skin skin;
	private Demo demo;
	private Table demoPlaceholder;
	
	@Override
	public void create () 
	{
		config = new PdConfiguration();
		Pd.audio.create(config);

		// new ScreenViewport()
		stage = new Stage(new FitViewport(640, 480));
		
		skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
		
		Table root = new Table();
		root.defaults().pad(10);
		
		Table header = new Table(skin);
		Image icon = new Image(new Texture(Gdx.files.internal("logo.png")));
		
		icon.setScaling(Scaling.none);
		
		header.add(icon).padRight(20);
		
		final SelectBox<Demo> demoSelector = new SelectBox<Demo>(skin);
		
		demoSelector.setItems(new Demo[]{
			new IntroDemo(),
			new MidiMusicDemo(),
			new AtmosphereDemo(),
			new SoundEffectsDemo(),
			new MicAnalysisDemo(),
			new MicProcessingDemo()
		});
		
		demoPlaceholder = new Table(skin);
		
		header.add(demoSelector);
		root.add(header).row();
		root.add(demoPlaceholder).expand();
		
		stage.addActor(root);
		
		root.setFillParent(true);
		
		demoSelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setDemo(demoSelector.getSelected());
			}
		});
		
		setDemo(demoSelector.getSelected());
		
		Gdx.input.setInputProcessor(stage);
	}
	
	private void setDemo(Demo newDemo)
	{
		if(demo != null){
			demo.dispose();
		}
		demoPlaceholder.clear();
		demo = newDemo;
		if(demo != null){
			demoPlaceholder.add(demo.create(skin));
		}
	}

	@Override
	public void render () {
		float b = .3f;
		Gdx.gl.glClearColor(b,b,b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
	}
	
	@Override
	public void dispose () 
	{
		if(demo != null){
			demo.dispose();
		}
		stage.dispose();
		Pd.audio.release();
	}
	
	@Override
	public void pause() {
		if(Gdx.app.getType() == ApplicationType.Android){
			if(demo != null){
				demo.pause();
			}
			Pd.audio.pause();
		}
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
		if(Gdx.app.getType() == ApplicationType.Android){
			Pd.audio.resume();
			if(demo != null){
				demo.resume();
			}
		}
	}
}
