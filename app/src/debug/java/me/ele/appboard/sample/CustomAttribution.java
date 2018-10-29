package me.ele.appboard.sample;

import java.util.ArrayList;
import java.util.List;

import me.ele.appboard.base.Element;
import me.ele.appboard.base.IAttrs;
import me.ele.appboard.base.item.Item;
import me.ele.appboard.base.item.TextItem;

public class CustomAttribution implements IAttrs {

    @Override
    public List<Item> getAttrs(Element element) {
        List<Item> items = new ArrayList<>();
        if (element.getView() instanceof CustomView) {
            CustomView view = (CustomView) element.getView();
            items.add(new TextItem("More", view.getMoreAttribution()));
        }
        return items;
    }
}
