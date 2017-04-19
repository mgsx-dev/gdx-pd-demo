package net.mgsx.pd.demo;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.mgsx.pd.GdxPdDemo;
import net.mgsx.pd.Pd;

public class SettingsDemo implements Demo
{
	@Override
	public Actor create(Skin skin) 
	{
		Pd.audio.pause();
		
		Table root = new Table(skin);
		
		String info = "Settings\n- change audio settings -";
		
		Label infoLabel = new Label(info, skin, "title");
		infoLabel.setAlignment(Align.center);
		
		root.add(infoLabel).padBottom(30).colspan(4).row();

		root.defaults().pad(10);
		
		int bufferSizeExp = (int)MathUtils.log2(GdxPdDemo.config.bufferSize);
		
		addBufferSizeControl(root, "Buffer Size", 6, 16, bufferSizeExp);
		addBufferCountControl(root, "Buffer Count", 1, 64, GdxPdDemo.config.bufferCount);
		
		return root;
	}
	
	private void addBufferSizeControl(Table table, String label, int min, int max, int value)
	{
		int realValue = 1 << value;
		final Label dynLabel = new Label(String.valueOf(realValue), table.getSkin());
		final Slider control = new Slider(min, max, 1, false, table.getSkin());
		control.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int realValue = 1 << (int)control.getValue();
				dynLabel.setText(String.valueOf(realValue));
				GdxPdDemo.config.bufferSize = realValue;
			}
		});
		control.setValue(value);
		
		table.add(label);
		table.add(control);
		table.add(dynLabel);
		table.row();
	}

	private void addBufferCountControl(Table table, String label, int min, int max, int value)
	{
		final Label dynLabel = new Label(String.valueOf(value), table.getSkin());
		final Slider control = new Slider(min, max, 1, false, table.getSkin());
		control.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				dynLabel.setText(String.valueOf((int)control.getValue()));
				GdxPdDemo.config.bufferCount = (int)control.getValue();
			}
		});
		control.setValue(value);
		
		table.add(label);
		table.add(control);
		table.add(dynLabel);
		table.row();
	}

	@Override
	public void dispose() {
		Pd.audio.resume();
	}
	
	@Override
	public String toString() {
		return "[Settings]";
	}

}
