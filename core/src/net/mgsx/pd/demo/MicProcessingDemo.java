package net.mgsx.pd.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.mgsx.pd.GdxPdDemo;
import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;

public class MicProcessingDemo implements Demo
{
	private PdPatch patch;
	
	private ButtonGroup<Button> group;
	
	@Override
	public Actor create(Skin skin) 
	{
		Pd.audio.pause();
		GdxPdDemo.config.inputChannels = 1;
		Pd.audio.resume();
		
		patch = Pd.audio.open(Gdx.files.internal("pd/pitchshift.pd"));
		
		group = new ButtonGroup<Button>();
		group.setMaxCheckCount(1);
		group.setMinCheckCount(0);
		
		float pad = 40;
		
		Table root = new Table(skin);
		
		String info = "You want to play Vador?\n" +
				"- realtime audio processing -";
		
		Label infoLabel = new Label(info, skin, "title");
		infoLabel.setAlignment(Align.center);
		root.add(infoLabel).padBottom(30).row();
		
		
		final Slider pitchSlider = new Slider(0.5f, 2f, .01f, false, skin);
		final Label pitchLabel = new Label("", skin);
		
		Table psTable = new Table(skin);
		psTable.defaults().padRight(10);
		psTable.add(pitchSlider);
		psTable.add(pitchLabel);
		
		pitchSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendFloat("pitch", pitchSlider.getValue());
				pitchLabel.setText(String.format("%.02f", pitchSlider.getValue()));
			}
		});
		pitchSlider.setValue(0.75f);
		
		root.add("Headset is highly recommanded in realtime mode to prevent larsen.").padTop(pad).row();
		pdButton(root, "Realtime", skin, "mode", 0, true);
		root.row();
		
		root.add("You could record and replay microphone input instead (up to 10s).").padTop(pad).row();
		Table nortTable = new Table(skin);
		nortTable.defaults().padRight(10);
		pdButton(nortTable, "Record", skin, "mode", 1, true);
		pdButton(nortTable, "Play", skin, "mode", 2, false);
		pdButton(nortTable, "Stop", skin, "mode", 3, false);
		root.add(nortTable).row();
		
		root.add("Vador's auto breath kicks in to fill silence").padTop(pad).row();
		pdToggle(root, "Vador", skin, "vador");
		root.row();
		
		root.add("Adjust realtime pitch shifter for funny effects.").padTop(pad).row();
		root.add(psTable).row();
		
		return root;
	}
	
	private Actor pdButton(Table table, String label, Skin skin, final String recv, final int msg, final boolean inGroup)
	{
		final TextButton button = new TextButton(label, skin, inGroup ? "toggle" : "default");
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(button.isChecked() || !inGroup){
					Pd.audio.sendFloat(recv, msg);
				}
			}
		});
		group.add(button);
		table.add(button);
		return table;
	}

	private Actor pdToggle(Table table, String label, Skin skin, final String recv)
	{
		final TextButton button = new TextButton(label, skin, "toggle");
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendList(recv, button.isChecked() ? 1 : 0);
			}
		});
		table.add(button);
		return table;
	}

	@Override
	public void dispose() {
		Pd.audio.close(patch);
		
		Pd.audio.pause();
		GdxPdDemo.config.inputChannels = 0;
		Pd.audio.resume();
	}
	
	@Override
	public String toString() {
		return "Mic Processing";
	}
	
}
