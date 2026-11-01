package dev.battleplacks.client.render;

/** One stacked piece of a card (tile, trainer, party, label). */
public interface CardElement {
    int height();

    void draw(CardContext c, int y);
}
