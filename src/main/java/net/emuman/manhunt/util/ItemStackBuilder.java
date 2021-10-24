package net.emuman.manhunt.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

/**
 * A wrapper for ItemStack that allows for repeated calls and the ability to easily assign ItemMeta attributes.
 */
public class ItemStackBuilder {

    private final ItemStack finalStack;
    private final ItemMeta meta;

    /**
     * Creates a new ItemStackBuilder.
     *
     * @param material the material to base the ItemStackBuilder off of.
     */
    public ItemStackBuilder(Material material) {
        this.finalStack = new ItemStack(material);
        this.meta = finalStack.getItemMeta();
    }

    /**
     * Creates a new ItemStackBuilder.
     *
     * @param itemStack the ItemStack to base the ItemStackBuilder off of (including the existing ItemMeta).
     */
    public ItemStackBuilder(ItemStack itemStack) {
        this.finalStack = itemStack.clone();
        this.meta = finalStack.getItemMeta();
    }

    /**
     * @param amount the amount of items contained in the ItemStack.
     * @return       this same instance for repeated calls.
     */
    public ItemStackBuilder setAmount(int amount) {
        finalStack.setAmount(amount);
        return this;
    }

    /**
     * @param name the name of the ItemStack.
     * @return     this same instance for repeated calls.
     */
    public ItemStackBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    /**
     * @param isUnbreakable true if the ItemStack should be unbreakable, false otherwise.
     * @return              this same instance for repeated calls.
     */
    public ItemStackBuilder setUnbreakable(boolean isUnbreakable) {
        meta.setUnbreakable(isUnbreakable);
        return this;
    }

    /**
     * @param lore the lore contained in the ItemStack.
     * @return     this same instance for repeated calls.
     */
    public ItemStackBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    /**
     * @param attribute the attribute to be modified.
     * @param modifier  the modifier applied to the attribute.
     * @return          this same instance for repeated calls.
     */
    public ItemStackBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * @param itemFlags any ItemFlags that should be set.
     * @return          this same instance for repeated calls.
     */
    public ItemStackBuilder addItemFlags(ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    /**
     * @param enchantment            the enchantment to apply to the ItemStack.
     * @param level                  the level of the enchantment to apply.
     * @param ignoreLevelRestriction true if the enchantment level should ignore the level restriction, false otherwise.
     * @return                       this same instance for repeated calls.
     */
    public ItemStackBuilder addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    /**
     * @param data the custom model data value to set for the ItemStack.
     * @return     this same instance for repeated calls.
     */
    public ItemStackBuilder setCustomModelData(int data) {
        meta.setCustomModelData(data);
        return this;
    }

    /**
     * Builds the ItemStack, returning a copy for use.
     *
     * @return a copy of the built ItemStack.
     */
    public ItemStack build() {
        finalStack.setItemMeta(meta);
        return finalStack.clone();
    }

    /**
     * Creates new leather armor with the specified color, contained within a single method.
     *
     * @param armorType the armor piece to use (must be a leather armor piece)
     * @param color     the color of the new armor.
     * @return          the ItemStack with the new colored armor, false if armorType was invalid.
     */
    public static ItemStack newLeatherArmor(Material armorType, Color color) {
        if (armorType != Material.LEATHER_HELMET &&
                armorType != Material.LEATHER_CHESTPLATE &&
                armorType != Material.LEATHER_LEGGINGS &&
                armorType != Material.LEATHER_BOOTS) return null;
        ItemStack armorPiece = new ItemStack(armorType);
        LeatherArmorMeta meta = (LeatherArmorMeta) armorPiece.getItemMeta();
        assert meta != null;
        meta.setColor(color);
        armorPiece.setItemMeta(meta);
        return armorPiece;
    }

}
