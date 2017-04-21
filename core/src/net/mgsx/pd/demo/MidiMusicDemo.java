package net.mgsx.pd.demo;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import net.mgsx.midi.sequence.MidiSequence;
import net.mgsx.midi.sequence.event.meta.Tempo;
import net.mgsx.pd.Pd;
import net.mgsx.pd.midi.MidiMusic;
import net.mgsx.pd.patch.PdPatch;

public class MidiMusicDemo extends DemoBase
{
	private PdPatch patch;
	private MidiMusic music;
	
	private Slider tempoController;
	private boolean updatingPosition;
	 
	@Override
	public Actor create(Skin skin) 
	{
		Table root = new Table(skin);
		
		final SelectBox<FileHandle> songSelector = new SelectBox<FileHandle>(skin);
		Array<FileHandle> files = new Array<FileHandle>(Gdx.files.internal("music").list(".mid"));
		files.sort(new Comparator<FileHandle>() {
			@Override
			public int compare(FileHandle o1, FileHandle o2) {
				return o1.name().compareTo(o2.name());
			}
		});
		songSelector.setItems(files);
		
		songSelector.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				changeSong(songSelector.getSelected());
			}
		});
		
		tempoController = new Slider(60, 240, 1, false, skin);
		tempoController.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(music != null){
					music.setBPM(tempoController.getValue());
				}
			}
		});
		
		final Slider positionController = new Slider(0, 1, .01f, false, skin){
			@Override
			public void act(float delta) {
				super.act(delta);
				updatingPosition = true;
				setValue(music.getPosition() / music.getDuration());
				updatingPosition = false;
			}
		};
		positionController.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(!updatingPosition){
					music.setPosition(positionController.getValue() * music.getDuration());
				}
			}
		});
		
		final Slider reverbController = new Slider(0, 1, .01f, false, skin);
		reverbController.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendFloat("reverb", reverbController.getValue());
			}
		});
		
		final TextButton btPlayStop = new TextButton("Stop", skin);
		btPlayStop.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(music.getPosition() >= music.getDuration()){
					music.stop();
				}
				if(music.isPlaying()){
					btPlayStop.setText("Play");
					music.stop();
				}else{
					btPlayStop.setText("Stop");
					music.play();
				}
				
			}
		});
		
		String info = "More control on music\n- full procedural synthetizers and MIDI files -";
		Label infoLabel = new Label(info, skin, "title");
		infoLabel.setAlignment(Align.center);
		
		root.defaults().padTop(15);
		
		root.add(infoLabel).colspan(2).padBottom(10);
		root.row();
		
		root.add("Song");
		root.add(songSelector);
		root.row();
		
		root.add("Tempo");
		root.add(tempoController);
		root.row();
		
		root.add("Position");
		root.add(positionController);
		root.row();
		
		root.add("Reverb");
		root.add(reverbController);
		root.row();
		
		root.add("Playback");
		root.add(btPlayStop);
		root.row();

		Table licence = new Table(skin);
		float licencePad = 10;
		licence.add("Song from").padRight(licencePad);
		licence.add(link("50 MIDI Tunes", "https://opengameart.org/content/50-midi-tunes", skin));
		licence.add("by A.J. Gillespie is licensed under").padRight(licencePad).padLeft(licencePad);
		licence.add(link("CC BY 3.0", "https://creativecommons.org/licenses/by/3.0/", skin)).row();
		
		
		root.add(licence).colspan(2).padTop(60);
		root.row();
		
		patch = Pd.audio.open(Gdx.files.internal("pdmidi/midiplayer.pd"));

		reverbController.setValue(0.1f);
		
		changeSong(songSelector.getSelected());
		
		return root;
	}
	
	private static Actor link(String label, final String url, Skin skin) 
	{
		Label actor = new Label(label, skin, "link");
		actor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.net.openURI(url);
			}
		});
		return actor;
	}

	
	protected void changeSong(FileHandle file) 
	{
		if(music != null){
			music.dispose();
		}
		MidiSequence sequence = new MidiSequence(file);
		
		// get first tempo change
		for(Tempo event : sequence.findEvents(new Array<Tempo>(), Tempo.class)){
			tempoController.setValue(event.getBpm());
			break;
		}
		
		// create and play music with default sequencer
		music = Pd.midi.createMidiMusic(sequence);
		music.play();
	}

	@Override
	public void dispose() {
		music.dispose();
		Pd.audio.close(patch);
	}
	
	@Override
	public String toString() {
		return "Music";
	}
	
	@Override
	public void pause() {
		if(music != null){
			music.pause();
		}
	}
	
	@Override
	public void resume() {
		if(music != null){
			music.play();
		}
	}

}
