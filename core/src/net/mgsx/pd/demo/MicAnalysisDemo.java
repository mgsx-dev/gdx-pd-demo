package net.mgsx.pd.demo;

import org.puredata.core.PdListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.mgsx.pd.GdxPdDemo;
import net.mgsx.pd.Pd;
import net.mgsx.pd.patch.PdPatch;
import net.mgsx.pd.utils.PdAdapter;

public class MicAnalysisDemo extends DemoBase
{
	private PdPatch patch;
	
	@Override
	public Actor create(Skin skin) {
		
		Pd.audio.pause();
		GdxPdDemo.config.inputChannels = 1;
		Pd.audio.resume();
		
		patch  = Pd.audio.open(Gdx.files.internal("pd/breath.pd"));
		
		float pad = 60;
		Table root = new Table(skin);
		
		String info = "Listen to players\n" +
				"- realtime audio analysis -";
		
		Label infoLabel = new Label(info, skin, "title");
		infoLabel.setAlignment(Align.center);
		root.add(infoLabel).row();
		
		Table analysisTable = new Table(skin);
		analysisTable.defaults().padRight(10);
		
		analysisTable.add("Make some noise to test envelop tracking").padTop(pad).colspan(2).row();
		
		pdMetric(analysisTable, "Level (db)", "level", 0, 120, skin);
		analysisTable.row();
		
		analysisTable.add("Sing or whistle to test pitch recognition").padTop(pad).colspan(2).row();
		
		pdMetric(analysisTable, "Pitch (note)", "pitch", 0, 127, skin);
		analysisTable.row();
		
		root.add(analysisTable).row();
		
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
