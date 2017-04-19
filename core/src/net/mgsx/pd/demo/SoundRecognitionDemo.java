package net.mgsx.pd.demo;

import org.puredata.core.PdListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.mgsx.pd.GdxPdDemo;
import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;
import net.mgsx.pd.utils.PdAdapter;

public class SoundRecognitionDemo implements Demo
{
	private PdPatch patch;
	
	@Override
	public Actor create(Skin skin) {
		
		Pd.audio.pause();
		GdxPdDemo.config.inputChannels = 1;
		Pd.audio.resume();
		
		patch  = Pd.audio.open(Gdx.files.internal("pd/breath.pd"));
		
		Table root = new Table(skin);
		
		String info = "Listen to players\n" +
				"- realtime audio analysis -";
		
		Label infoLabel = new Label(info, skin, "title");
		infoLabel.setAlignment(Align.center);
		root.add(infoLabel).row();
		
		root.add("SOUND ANALYSIS").padTop(40).row();
		
		Table analysisTable = new Table(skin);
		analysisTable.defaults().padRight(10);
		pdMetric(analysisTable, "Level (db)", "level", 0, 120, skin);
		analysisTable.row();
		pdMetric(analysisTable, "Pitch (note)", "pitch", 0, 127, skin);
		analysisTable.row();
		
		root.add(analysisTable).row();
		
		
		root.add("SOUND RECOGNITION").padTop(40).row();
		
		Table recogTable = new Table(skin);
		recogTable.defaults().padRight(10);
		recogTable.add(pdBongHeader(skin)).colspan(2).row();
		recogTable.add(pdBonk(skin)).padTop(20).padBottom(20).width(500).height(100).colspan(2).row();
		recogTable.add(pdButton("start learning", skin, "start_learning"));
		recogTable.add(pdButton("stop learning", skin, "stop_learning")).row();
		
		
//		recogTable.add("2- produce a sound 3 times in a row").expandX().left().colspan(2);
//		recogTable.row();
//		
//		recogTable.add("3- repeat 2 at will...").expandX().left();
//		recogTable.row();
		
		
		
//		recogTable.add("The attack tracker is now trained, attackId from mic input\n" + 
//				"should be coherent with the data gathered while training.").colspan(2).row();
		
		root.add(recogTable);
		
		return root;
	}

	@Override
	public void dispose() {
		patch.dispose();
		
		Pd.audio.pause();
		GdxPdDemo.config.inputChannels = 0;
		Pd.audio.resume();
	}

	@Override
	public String toString() {
		return "Mic Controller";
	}
	
	private Actor pdButton(String label, Skin skin, final String receiver)
	{
		final TextButton text = new TextButton(label,skin);
		text.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Pd.audio.sendList(receiver, 0.5f);
			}
		});
		return text;
	}
	
	private Actor pdBongHeader(Skin skin)
	{
		return new Label("- please follow instructions below -", skin);
	}
	
	private Actor pdBonk(Skin skin)
	{
		final String infoInit = "Press start to begin a new learning process.";
		
		final Label label = new Label(infoInit, skin);
		label.setAlignment(Align.center);
		
		PdListener statusListener = new PdAdapter(){
			@Override
			public void receiveFloat(String source, float x) {
				if(x > 0){
					label.setText("Make a sound like clap your hand or your finger.");
				}else{
					label.setText("Training is now over, make some sounds to test recognition.");
				}
			}
		};
		
		PdListener bonkListener = new PdAdapter(){
			@Override
			public void receiveList(String source, Object... args) {
				float state = (Float)args[0];
				float occurence = (Float)args[1];
				float id = (Float)args[2];
				
				if(state > 0)
				{
					if(occurence >= 2){
						label.setText("Learning for sound [" + String.valueOf((int)id) + "] complete.\nTry a different sound from earlier\nor press stop when you have enough sounds.");
					}else{
						label.setText("Learning for sound [" + String.valueOf((int)id) + "] in progress [" + String.valueOf((int)occurence + 1) + " / 3].\nMake the same sound again...");
					}
				}
				else if(state > -1)
				{
					label.setText("Sound id [" + String.valueOf((int)id) + "] recognized!\nMake a sound again to test recognition\nor press start to begin a new learning process.");
				}
			}
		};
		
		Pd.audio.addListener("status", statusListener);
		Pd.audio.addListener("bonk", bonkListener);
		return label;
	}

	private Actor pdMetric(final Table table, final String title, final String symbol, float min, float max, Skin skin)
	{
		final Label dynLabel = new Label("", skin);
		final ProgressBar pb = new ProgressBar(min, max, (max - min) / 1000, false, skin);
		PdListener listener = new PdAdapter(){
			@Override
			public void receiveFloat(String source, float x) {
				if(x > 0){
					dynLabel.setText(String.valueOf(MathUtils.round(x)));
					pb.setValue(x);
				}
			}
		};
		Pd.audio.addListener(symbol, listener);
		table.add(title);
		table.add(pb);
		table.add(dynLabel);
		return table;
	}

}
