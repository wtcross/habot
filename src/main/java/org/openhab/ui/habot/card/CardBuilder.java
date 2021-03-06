package org.openhab.ui.habot.card;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.CoreItemFactory;
import org.eclipse.smarthome.core.transform.TransformationHelper;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.StateDescription;
import org.openhab.ui.habot.card.internal.CardRegistry;
import org.openhab.ui.habot.nlp.Intent;
import org.openhab.ui.habot.nlp.internal.skill.HistoryLastChangesSkill;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Reference;

/**
 * Tries to come up with a {@link Card} to present with HABot's reply when asked about a set of entities.
 *
 * First, tries to look up in the registry ("card deck") for a card previously saved and having tags matching the
 * entities (for example: object:temperature + location:kitchen).
 *
 * If so, simply use that {@link Card} - this allows the user to control exactly what they want to see. If no card was
 * found, build one on the fly using the provided matching items. Note that the user will be allowed the opportunity to
 * add the ad-hoc card in the card deck, with the appropriate tags, and edit it so that it would appear with the
 * eventual customizations the next time.
 *
 * @author Yannick Schaus - Initial contribution
 *
 */
@org.osgi.service.component.annotations.Component(service = CardBuilder.class, immediate = true)
public class CardBuilder {

    private CardRegistry cardRegistry;
    private ItemRegistry itemRegistry;

    @Reference
    protected void setCardRegistry(CardRegistry cardRegistry) {
        this.cardRegistry = cardRegistry;
    }

    protected void unsetCardRegistry(CardRegistry cardRegistry) {
        this.cardRegistry = null;
    }

    @Reference
    public void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void unsetItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = null;
    }

    public Card buildCard(Intent intent, List<Item> matchedItems) {
        Set<String> tags = intent.getEntities().entrySet().stream()
                .filter(e -> e.getKey().equals("object") || e.getKey().equals("location"))
                .map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.toSet());

        Collection<Card> cardsInRegistry = this.cardRegistry.getCardByTags(tags).stream()
                .filter(c -> !c.isNotReuseableInChat()).collect(Collectors.toList());
        if (cardsInRegistry.size() > 0) {
            // don't handle multiple cards, just return the first one
            Card existingCard = cardsInRegistry.iterator().next();
            existingCard.updateTimestamp();
            cardRegistry.update(existingCard);
            return existingCard;
        }

        Card card = new Card("HbCard");
        card.addTags(tags);
        card.setEphemeral(true);
        card.addConfig("bigger", true);
        card.updateTimestamp();

        if (matchedItems.size() == 1) {
            Item item = matchedItems.get(0);
            card.setTitle(item.getLabel());
            card.setSubtitle(item.getName());

            switch (item.getType()) {
                case CoreItemFactory.SWITCH:
                    Component switchComponent = new Component("HbSwitch");
                    switchComponent.addConfig("item", item.getName());
                    card.addComponent("right", switchComponent);
                    break;
                case CoreItemFactory.DIMMER:
                    if (item.hasTag("habot:switchable")) {
                        Component dimmerSwitchComponent = new Component("HbSwitch");
                        dimmerSwitchComponent.addConfig("item", item.getName());
                        card.addComponent("right", dimmerSwitchComponent);
                    } else if (!item.hasTag("habot:control:knob")) {
                        Component dimmerValueComponent = new Component("HbSingleItemValue");
                        dimmerValueComponent.addConfig("item", item.getName());
                        card.addComponent("right", dimmerValueComponent);
                    }

                    Component dimmerContainerComponent = new Component("HbContainer");
                    dimmerContainerComponent.addConfig("classes", new String[] { "full-width", "text-center" });
                    if (item.hasTag("habot:control:knob")) {
                        Component knobComponent = new Component("HbKnob");
                        knobComponent.addConfig("item", item.getName());
                        knobComponent.addConfig("size", "200px");
                        knobComponent.addConfig("textSize", "2rem");
                        knobComponent.addConfig("color", "primary");
                        dimmerContainerComponent.addComponent("main", knobComponent);
                    } else {
                        Component sliderComponent = new Component("HbSlider");
                        sliderComponent.addConfig("item", item.getName());
                        dimmerContainerComponent.addComponent("main", sliderComponent);
                    }

                    card.addComponent("main", dimmerContainerComponent);
                    break;
                case CoreItemFactory.ROLLERSHUTTER:
                    Component shutterValueComponent = new Component("HbSingleItemValue");
                    shutterValueComponent.addConfig("item", item.getName());
                    card.addComponent("right", shutterValueComponent);
                    Component shutterContainerComponent = new Component("HbContainer");
                    shutterContainerComponent.addConfig("classes", new String[] { "full-width", "text-center" });
                    Component shutterControlComponent = new Component("HbShutterControl");
                    shutterControlComponent.addConfig("item", item.getName());
                    shutterControlComponent.addConfig("size", "lg");
                    shutterControlComponent.addConfig("rounded", true);
                    shutterControlComponent.addConfig("glossy", true);
                    shutterControlComponent.addConfig("push", true);
                    shutterControlComponent.addConfig("stopIcon", "close");
                    shutterContainerComponent.addComponent("main", shutterControlComponent);
                    card.addComponent("main", shutterContainerComponent);
                    break;
                case CoreItemFactory.PLAYER:
                    Component playerContainerComponent = new Component("HbContainer");
                    playerContainerComponent.addConfig("classes", new String[] { "full-width", "text-center" });
                    Component playerComponent = new Component("HbPlayer");
                    playerComponent.addConfig("item", item.getName());
                    playerComponent.addConfig("size", "lg");
                    playerContainerComponent.addComponent("main", playerComponent);
                    card.addComponent("main", playerContainerComponent);
                    break;
                case CoreItemFactory.COLOR:
                    Component colorPickerComponent = new Component("HbColorPicker");
                    colorPickerComponent.addConfig("item", item.getName());
                    card.addComponent("right", colorPickerComponent);
                    Component brightnessDimmerComponent = new Component("HbSwitch");
                    brightnessDimmerComponent.addConfig("item", item.getName());
                    card.addComponent("right", brightnessDimmerComponent);
                    Component brightnessDimmerContainerComponent = new Component("HbContainer");
                    brightnessDimmerContainerComponent.addConfig("classes",
                            new String[] { "full-width", "text-center" });
                    Component brightnessSliderComponent = new Component("HbSlider");
                    brightnessSliderComponent.addConfig("item", item.getName());
                    brightnessDimmerContainerComponent.addComponent("main", brightnessSliderComponent);
                    card.addComponent("main", brightnessDimmerContainerComponent);
                    break;
                default:
                    if (item.getType() == CoreItemFactory.IMAGE
                            || item.getTags().stream().anyMatch(t -> t.startsWith("habot:image:sitemap:"))) {
                        /*
                         * If the item is an image (or a String with a tag indicating it's an image), build a
                         * HbImage component in the "media" slot
                         */
                        Component singleImageComponent = new Component("HbImage");
                        singleImageComponent.addConfig("item", item.getName());
                        card.addComponent("media", singleImageComponent);
                    } else {
                        /*
                         * Try to get a formatted state to determine whether it's small enough to display
                         * in the "right" slot - otherwise add it to the "main" slot
                         */
                        String formattedState = formatState(item, item.getState());
                        Component singleItemComponent = new Component("HbSingleItemValue");
                        singleItemComponent.addConfig("item", item.getName());
                        if (formattedState.length() < 10) {
                            card.addComponent("right", singleItemComponent);
                        } else {
                            card.addComponent("main", singleItemComponent);
                        }
                    }
                    break;
            }

        } else {
            card.setTitle(getCardTitleFromGroupLabels(tags));
            card.setSubtitle(matchedItems.size() + " items"); // TODO: i18n

            // TODO: detect images and build a HbCarousel with them - for webcams etc.

            Component list = new Component("HbList");
            for (Item item : matchedItems) {
                Component listItem = new Component("HbListItem");
                listItem.addConfig("item", item.getName());
                listItem.addConfig("label", item.getLabel());

                list.addComponent("items", listItem);
            }

            card.addComponent("list", list);
        }

        cardRegistry.add(card);
        return card;
    }

    public Card buildChartCard(Intent intent, List<Item> matchedItems, String period) {
        Set<String> tags = intent.getEntities().entrySet().stream().map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.toSet());

        Card card = new Card("HbCard");
        card.addTags(tags);
        card.setEphemeral(true);
        card.addConfig("bigger", true);
        card.updateTimestamp();

        if (matchedItems.size() == 1) {
            Item item = matchedItems.get(0);
            card.setTitle(item.getLabel());
            card.setSubtitle(item.getName());
        } else {
            card.setTitle(getCardTitleFromGroupLabels(tags));
            card.setSubtitle(matchedItems.size() + " items"); // TODO: i18n
        }

        Component chart = new Component("HbChartImage");
        chart.addConfig("items",
                matchedItems.stream().map(i -> i.getName()).collect(Collectors.toList()).toArray(new String[0]));
        chart.addConfig("period", period);

        Component analyzeButton = new Component("HbAnalyzeActionButton");
        analyzeButton.addConfig("items", chart.getConfig().get("items"));
        analyzeButton.addConfig("period", chart.getConfig().get("period"));

        card.addComponent("media", chart);
        card.addComponent("actions", analyzeButton);

        cardRegistry.add(card);
        return card;
    }

    private String getCardTitleFromGroupLabels(Set<String> tags) {
        Collection<Item> groups = this.itemRegistry.getItemsByTagAndType("Group", tags.stream().toArray(String[]::new));
        if (groups.isEmpty()) {
            return "";
        } else {
            return groups.stream().map(i -> i.getLabel()).collect(Collectors.joining(", "));
        }
    }

    private String formatState(Item item, State state) {
        if (item.getStateDescription() != null) {
            StateDescription stateDescription = item.getStateDescription();
            if (stateDescription != null && stateDescription.getPattern() != null) {
                try {
                    String transformedState = TransformationHelper.transform(
                            FrameworkUtil.getBundle(HistoryLastChangesSkill.class).getBundleContext(),
                            stateDescription.getPattern(), state.toString());
                    if (transformedState.equals(state.toString())) {
                        return state.format(stateDescription.getPattern());
                    } else {
                        return transformedState;
                    }
                } catch (NoClassDefFoundError ex) {
                    // TransformationHelper is optional dependency, so ignore if class not found
                    // return state as it is without transformation
                    return state.toString();
                }
            } else {
                return state.toString();
            }
        } else {
            return state.toString();
        }
    }
}
