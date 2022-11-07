package ru.nsu.ccfit.zuev.osu.online;

import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene.ITouchArea;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.HorizontalAlign;

import ru.nsu.ccfit.zuev.osu.ResourceManager;
import ru.nsu.ccfit.zuev.osu.Utils;

public class SendingPanel extends Rectangle {
    private ChangeableText mapText, rankText, accText, scoreText;
    private ChangeableText buttonText;
    private Rectangle mapRect, rankRect, accRect, scoreRect;
    private Sprite button;

    private long rank, score;
    private float accuracy;
    private boolean canBeDismissed = false;

    public SendingPanel(long rank, long score, float accuracy) {
        super(0, Utils.toRes(-300), Utils.toRes(800), Utils.toRes(300));
        TextureRegion btnTex = ResourceManager.getInstance().
                getTexture("ranking_button");

        this.rank = rank;
        this.score = score;
        this.accuracy = accuracy;
        setColor(0, 0, 0, 0.7f);

        button = new Sprite(Utils.toRes(272), Utils.toRes(300), btnTex) {


            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
                                         float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (canBeDismissed) {
                    SendingPanel.this.registerEntityModifier(new MoveYModifier(0.5f, 0,
                            Utils.toRes(-350)));
                    canBeDismissed = false;
                    return true;
                }
                return false;
            }
        };
        attachChild(button);

        buttonText = new ChangeableText(Utils.toRes(340), Utils.toRes(305),
                ResourceManager.getInstance().getFont("font"),
                "Sending...", HorizontalAlign.CENTER, 10);
        attachChild(buttonText);

        Text topScoreText = new Text(0, 0,
                ResourceManager.getInstance().getFont("CaptionFonrt"), "Overall Ranking");
        topScoreText.setPosition(Utils.toRes(400) - topScoreText.getWidth() / 2, Utils.toRes(60));
        attachChild(topScoreText);

        Text tableCaption = new Text(Utils.toRes(60), Utils.toRes(120),
                ResourceManager.getInstance().getFont("font"),
                "Map rank     Overall      Accuracy       Ranked score");
        attachChild(tableCaption);

        mapRect = new Rectangle(Utils.toRes(50), Utils.toRes(160),
                Utils.toRes(140), Utils.toRes(80));
        mapRect.setColor(1, 1, 0, 0.8f);
        attachChild(mapRect);

        rankRect = new Rectangle(Utils.toRes(195), Utils.toRes(160),
                Utils.toRes(150), Utils.toRes(80));
        attachChild(rankRect);

        accRect = new Rectangle(Utils.toRes(350), Utils.toRes(160),
                Utils.toRes(150), Utils.toRes(80));
        attachChild(accRect);

        scoreRect = new Rectangle(Utils.toRes(505), Utils.toRes(160),
                Utils.toRes(250), Utils.toRes(80));
        attachChild(scoreRect);

        Font font = ResourceManager.getInstance().getFont("font");
        mapText = new ChangeableText(0, 0, font, "#9999999", HorizontalAlign.CENTER, 8);
        placeText(mapRect, mapText);
        attachChild(mapText);

        rankText = new ChangeableText(0, 0, font, "#9999999\n(+100)", HorizontalAlign.CENTER, 19);
        placeText(rankRect, rankText);
        attachChild(rankText);

        accText = new ChangeableText(0, 0, font, "100.00%\n(+21.90%)", HorizontalAlign.CENTER, 16);
        placeText(accRect, accText);
        attachChild(accText);

        scoreText = new ChangeableText(0, 0, font, "99 123 456 789\n(+99 999 999)", HorizontalAlign.CENTER, 100);
        placeText(scoreRect, scoreText);
        attachChild(scoreText);
    }

    private void placeText(Rectangle rect, ChangeableText text) {
        text.setPosition(rect.getX() + rect.getWidth() / 2 - text.getWidth() / 2,
                rect.getY() + rect.getHeight() / 2 - text.getHeight() / 2);
    }

    private void setRectColor(Rectangle rect, float difference) {
        if (difference > 0)
            rect.setColor(0, 1, 0, 0.5f);
        else if (difference < 0)
            rect.setColor(1, 0, 0, 0.5f);
        else
            rect.setColor(0.4f, 0.4f, 0.4f, 0.8f);
    }

    private String formatScore(long score) {
        StringBuilder scoreBuilder = new StringBuilder();
        scoreBuilder.append(Math.abs(score));
        for (int i = scoreBuilder.length() - 3; i > 0; i -= 3) {
            scoreBuilder.insert(i, ' ');
        }
        if (score < 0) {
            scoreBuilder.insert(0, '-');
        }
        return scoreBuilder.toString();
    }

    public void show(long mapRank, long newScore, long newRank, float newAcc) {
        canBeDismissed = true;
        mapText.setText(String.format("#%d", mapRank));
        placeText(mapRect, mapText);
        if (newScore > score)
            mapRect.setColor(1, 1, 0, 0.8f);
        else
            setRectColor(mapRect, 0);

        if (newRank == rank)
            rankText.setText(String.format("#%d", rank));
        else if (newRank < rank)
            rankText.setText(String.format("#%d\n(+%d)", newRank, rank - newRank));
        else
            rankText.setText(String.format("#%d\n(%d)", newRank, rank - newRank));
        placeText(rankRect, rankText);
        setRectColor(rankRect, rank - newRank);

        if (Math.abs(newAcc - accuracy) < 0.0001f)
            accText.setText(String.format("%.2f%%", accuracy * 100f));
        else if (newAcc < accuracy)
            accText.setText(String.format("%.2f%%\n(%.2f%%)", newAcc * 100f, (newAcc - accuracy) * 100f));
        else
            accText.setText(String.format("%.2f%%\n(+%.2f%%)", newAcc * 100f, (newAcc - accuracy) * 100f));
        placeText(accRect, accText);
        setRectColor(accRect, newAcc - accuracy);

        if (newScore == score)
            scoreText.setText(String.format("%s", formatScore(score)));
        else if (newScore < score)
            scoreText.setText(String.format("%s\n(%s)", formatScore(newScore), formatScore(newScore - score)));
        else
            scoreText.setText(String.format("%s\n(+%s)", formatScore(newScore), formatScore(newScore - score)));
        placeText(scoreRect, scoreText);
        setRectColor(scoreRect, newScore - score);

        buttonText.setText(" Dismiss");

        registerEntityModifier(new MoveYModifier(0.5f, Utils.toRes(-300), 0));
    }

    void setFail() {
        buttonText.setText(" Failed");
    }

    public ITouchArea getDismissTouchArea() {
        return button;
    }
}
