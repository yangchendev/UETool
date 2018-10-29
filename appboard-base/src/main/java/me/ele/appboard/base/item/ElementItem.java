package me.ele.appboard.base.item;

import me.ele.appboard.base.Element;

public class ElementItem extends TitleItem {

    private Element element;

    public ElementItem(String name, Element element) {
        super(name);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}
