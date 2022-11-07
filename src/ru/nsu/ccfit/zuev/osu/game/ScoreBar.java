package ru.nsu.ccfit.zuev.osu.game;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;

import ru.nsu.ccfit.zuev.osu.ResourceManager;
import ru.nsu.ccfit.zuev.osu.Utils;
import ru.nsu.ccfit.zuev.osu.helper.AnimSprite;
import ru.nsu.ccfit.zuev.osu.scoring.StatisticV2;

public class ScoreBar extends GameObject {
    private static float speed = 0.75f;
    private final StatisticV2 stat;
    private final Sprite bg;
    private final Sprite colour;
    private final AnimSprite ki;
    private final float width;
    private float lasthp = 0;

    public ScoreBar(final GameObjectListener listener, final Scene scene,
                    final StatisticV2 stat) {
        this.stat = stat;
        bg = new Sprite(0, 0, ResourceManager.getInstance().getTexture(
                "scorebar-bg"));
        bg.setScaleCenter(0, 0);
		/*final int colourFrames = SkinManager.getFrames("scorebar-colour");
		colour = new AnimSprite(Utils.toRes(5), Utils.toRes(16),
				"scorebar-colour-", colourFrames, colourFrames);*/
        if (ResourceManager.getInstance().isTextureLoaded("scorebar-colour-0")) {
            List<String> loadedScoreBarTextures = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                if (ResourceManager.getInstance().isTextureLoaded("scorebar-colour-" + i))
                    loadedScoreBarTextures.add("scorebar-colour-" + i);
            }
            colour = new AnimSprite(Utils.toRes(5), Utils.toRes(16), loadedScoreBarTextures.size(),
                    loadedScoreBarTextures.toArray(new String[loadedScoreBarTextures.size()]));
        } else {
            colour = new Sprite(Utils.toRes(5), Utils.toRes(16),
                    ResourceManager.getInstance().getTexture("scorebar-colour"));
        }
        width = colour.getWidth();
		/*for (int i = 0; i < colour.getTextureRegionCount(); i++) {
			colour.setTextureRegion(i, colour.getTextureRegionAt(i).deepCopy());
		}*/
        ki = ResourceManager.getInstance().isTextureLoaded("scorebar-kidanger")
                ? new AnimSprite(0, 0, 0, "scorebar-ki", "scorebar-kidanger", "scorebar-kidanger2")
                : new AnimSprite(0, 0, 0, "scorebar-ki");
        ki.setPosition(Utils.toRes(5) + colour.getWidth() - ki.getWidth() / 2,
                Utils.toRes(16) + colour.getHeight() / 2 - Utils.toRes(58));

        scene.attachChild(ki, 0);
        scene.attachChild(colour, 0);
        scene.attachChild(bg, 0);
    }

    public void setVisible(final boolean visible) {
        bg.setVisible(visible);
        colour.setVisible(visible);
        ki.setVisible(visible);
    }


    @Override
    public void update(final float dt) {
        float hp = stat.getHp();
        if (Math.abs(hp - lasthp) > speed * dt) {
            hp = speed * dt * Math.signum(hp - lasthp) + lasthp;
        }

		/*for (int i = 0; i < colour.getTextureRegionCount(); i++) {
			colour.getTextureRegionAt(i).setWidth((int) (width * hp));
		}*/
        colour.setWidth(width * hp);

        ki.setPosition(Utils.toRes(5) + colour.getWidth() - ki.getWidth() / 2,
                Utils.toRes(16) + colour.getHeight() / 2 - ki.getHeight() / 2);
        //ki.setScale(hp>lasthp?1.2f:1);
        ki.setFrame(hp > 0.49 ? 0
                : hp > 0.24 ? 1
                : 2);
        lasthp = hp;
		/*if (hp < 0.5f) {
			if (hp < 0.25f) {
				ki.setFrame(2);
			} else {
				ki.setFrame(1);
			}
		} else {
			ki.setFrame(0);
		}*/
    }

    public void flush() {
        lasthp = stat.getHp();
        update(0);
    }
    
}
