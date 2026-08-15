package ru.berdinskiybear.armorhud.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.berdinskiybear.armorhud.ArmorHudMod;
import ru.berdinskiybear.armorhud.config.ArmorHudConfig;

import java.util.List;
import java.util.Optional;

import static ru.berdinskiybear.armorhud.ArmorHudMod.*;

@Mixin(Gui.class)
public abstract class MixinGui {
    @Shadow
    @Final
    private RandomSource random;

    @Unique
    private static final Identifier WARNING_TEXTURE = Identifier.fromNamespaceAndPath(MOD_ID, "warn.png");

    // Reuses vanilla's "core/gui" shader (same one RenderPipelines.GUI uses) with a
    // multiply blend function (result = src * dst) instead of the default translucent
    // blend, so drawing this over an already-rendered icon tints it toward the draw
    // color without needing any texture of its own. Built once - the GL backend caches
    // compiled pipelines by object identity, so rebuilding this per-frame would recompile
    // a shader every frame.
    @Unique
    private static final RenderPipeline DURABILITY_TINT_PIPELINE = RenderPipeline.builder()
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/durability_tint"))
            .withVertexShader("core/gui")
            .withFragmentShader("core/gui")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withBlend(new BlendFunction(SourceFactor.DST_COLOR, DestFactor.ZERO, SourceFactor.ONE, DestFactor.ZERO))
            .build();

    @Shadow
    protected abstract void renderSlot(GuiGraphics graphics, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed);

    @Shadow
    public abstract Font getFont();

    @Shadow
    @Final
    private static Identifier HOTBAR_SPRITE;

    @Shadow
    @Final
    private static Identifier HOTBAR_OFFHAND_LEFT_SPRITE;

    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    public void renderArmorHud(GuiGraphics graphics, DeltaTracker tickCounter, CallbackInfo ci) {
        Profiler.get().push(MOD_ID);

        // this was extracted to a different method to be able to return whenever I want
        // without messing up the profiler
        drawArmorHud(graphics, tickCounter);

        // pop this out of profiler
        Profiler.get().pop();
    }

    @Unique
    private void drawArmorHud(GuiGraphics graphics, DeltaTracker tickCounter) {
        ArmorHudConfig config = getManager().getConfig();
        if (!config.isEnabled()) return;

        Player player = getCameraPlayer();
        if (player == null) return;

        final Optional<Rect2i> rect = getWidgetRect(graphics, player);
        // return if there is nothing to draw
        if (rect.isEmpty()) return;

        // fetch armor items
        List<ItemStack> armorItems = getArmorItems(player);
        if (config.isReversed()) {
            armorItems = armorItems.reversed();
        }

        final int textureWidth = SIZE + ((armorItems.size() - 1) * STEP);

        // here I draw the slots
        graphics.pose().pushMatrix();
        graphics.pose().translate(rect.get().getX(), rect.get().getY());

        if (config.getOrientation() == ArmorHudConfig.Orientation.VERTICAL) {
            graphics.pose().rotate(Mth.HALF_PI).translate(0, -SIZE);
        }

        int color = ArmorHudMod.getBedrockifyCompat() != null ? ARGB.white(ArmorHudMod.getBedrockifyCompat().hudOpacity()) : 0xFFFFFFFF;

        switch (config.getStyle()) {
            case HOTBAR -> {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, 0, 0, textureWidth - 3, SIZE, color);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 182 - 3, 0, textureWidth - 3, 0, 3, SIZE, color);
            }
            case ROUNDED_CORNERS -> {
                if (armorItems.size() > 1) {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, 0, 0, 3, SIZE, color);
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 3, 0, 3, 0, textureWidth - 6, SIZE, color);
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, SIZE - 3, 1, textureWidth - 3, 0, 3, SIZE, color);
                } else {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, 0, 0, SIZE, SIZE, color);
                }
            }
            case ROUNDED -> {
                if (armorItems.size() > 1) {
                    int borderWidth = (SIZE - STEP) / 2;
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, 0, 0, SIZE - borderWidth, SIZE, color);
                    // nothing happens if slots <= 2
                    for (int i = 1; i < armorItems.size() - 1; i++) {
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, borderWidth, 1, borderWidth + i * STEP, 0, STEP, SIZE, color);
                    }
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 1, 1, textureWidth - STEP - borderWidth, 0, SIZE - borderWidth, SIZE, color);
                } else {
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, 0, 0, SIZE, SIZE, color);
                }
            }
            // case NONE -> // nothing to draw ^_^
        }
        graphics.pose().popMatrix();

        for (int i = 0; i < armorItems.size(); i++) {
            ItemStack stack = armorItems.get(i);
            int x = rect.get().getX();
            int y = rect.get().getY();

            switch (config.getOrientation()) {
                case HORIZONTAL -> x += (STEP * i);
                case VERTICAL -> y += (STEP * i);
            }

            // here I blend in slot icons if so tells the current config
            if (config.isIconsShown() && config.getWidgetShown().shouldDrawEmptySlots() && stack.isEmpty()) {
                int slotIndex = config.isReversed() ? 3 - i : i;
                Identifier identifier = InventoryMenuAccessor.getTEXTURE_EMPTY_SLOTS().get(SLOT_IDS[slotIndex]);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, x + 3, y + 3, 16, 16);
            }

            // here I draw the armour items
            this.renderSlot(graphics, x + 3, y + 3, tickCounter, player, stack, i + 1);

            // COLOR mode: multiply-tint the icon toward vanilla's durability gradient
            // (same green -> orange -> red color ItemStack#getBarColor() already drives
            // for the NUMERIC/PERCENTAGE text below). Drawn as a quad *after* renderSlot,
            // not as a "set color before / reset after" wrap: this MC version's GUI
            // renderer is a deferred submit-list, not immediate-mode, so there's no global
            // color state to wrap the call with. GuiRenderState detects that this quad's
            // bounds match the icon just submitted and layers it on top automatically.
            if (config.getDurabilityDisplay() == ArmorHudConfig.DurabilityDisplay.COLOR
                    && !stack.isEmpty() && stack.isDamageableItem()) {
                int barColor = ARGB.opaque(stack.getBarColor());
                // only 25% toward white (was 50%) so the multiply reads clearly as green/orange/red
                // instead of a washed-out pastel, while still leaving a hint of the icon's material color
                int tint = ARGB.srgbLerp(0.25F, barColor, 0xFFFFFFFF);
                graphics.fill(DURABILITY_TINT_PIPELINE, x + 3, y + 3, x + 19, y + 19, tint);

                // solid, unblended 2px border in the full-strength durability color, drawn opaque
                // (not multiplied) in the margin around the icon so it stays visible regardless of
                // the icon's own colors or the tint above
                graphics.fill(x + 1, y + 1, x + 21, y + 3, barColor); // top
                graphics.fill(x + 1, y + 19, x + 21, y + 21, barColor); // bottom
                graphics.fill(x + 1, y + 3, x + 3, y + 19, barColor); // left
                graphics.fill(x + 19, y + 3, x + 21, y + 19, barColor); // right
            }

            // when anchoring to the hotbar, we want the warning to be on the other side to avoid clipping with the hotbar
            ArmorHudConfig.Side extrasSide = config.getAnchor() == ArmorHudConfig.Anchor.HOTBAR ? config.getSide() : config.getSide().getOpposite();

            if (config.getAnchor().isTop() && config.getOrientation() == ArmorHudConfig.Orientation.HORIZONTAL) {
                y += SIZE;
            } else if (extrasSide == ArmorHudConfig.Side.RIGHT && config.getOrientation() == ArmorHudConfig.Orientation.VERTICAL) {
                x += SIZE;
            }

            if (config.getDurabilityDisplay() != ArmorHudConfig.DurabilityDisplay.BAR
                    && config.getDurabilityDisplay() != ArmorHudConfig.DurabilityDisplay.COLOR
                    && !stack.isEmpty() && stack.isDamageableItem()) {
                String dura = switch (config.getDurabilityDisplay()) {
                    case NUMERIC -> String.valueOf(stack.getMaxDamage() - stack.getDamageValue());
                    case PERCENTAGE -> {
                        double percentage = 1 - (double) stack.getDamageValue() / stack.getMaxDamage();
                        yield (int) Math.floor(percentage * 100) + "%";
                    }
                    case BAR, COLOR -> throw new IllegalStateException("unreachable");
                };
                int textHeight = this.getFont().lineHeight;

                if (config.getOrientation() == ArmorHudConfig.Orientation.HORIZONTAL) {
                    if (!config.getAnchor().isTop()) y -= textHeight;
                    graphics.drawCenteredString(this.getFont(), dura, x + (SIZE / 2), y, ARGB.opaque(stack.getBarColor()));
                    if (config.getAnchor().isTop()) y += textHeight;
                } else {
                    int textWidth = this.getFont().width(dura) + 2;
                    int textY = (SIZE - textHeight) / 2;

                    if (extrasSide == ArmorHudConfig.Side.LEFT) x -= textWidth;
                    graphics.drawString(this.getFont(), dura, x + 1, y + textY, ARGB.opaque(stack.getBarColor()));
                    if (extrasSide == ArmorHudConfig.Side.RIGHT) x += textWidth;
                }
            }

            // here I draw warning icons if necessary
            if (config.isWarningShown() && shouldShowWarning(stack)) {
                if (config.getWarningBobIntensity() != 0) {
                    int intensity = config.getWarningBobIntensity();
                    y += (int) (this.random.nextInt(intensity) - Math.ceil(intensity / 2F));
                }

                if (config.getOrientation() == ArmorHudConfig.Orientation.HORIZONTAL) {
                    if (!config.getAnchor().isTop()) y -= WARNING_SIZE + 2;

                    int warnX = (SIZE - WARNING_SIZE) / 2;
                    graphics.blit(RenderPipelines.GUI_TEXTURED, WARNING_TEXTURE, x + warnX, y + 1, 0, 0, WARNING_SIZE, WARNING_SIZE, WARNING_SIZE, WARNING_SIZE);
                } else {
                    if (extrasSide == ArmorHudConfig.Side.LEFT) x -= WARNING_SIZE + 2;

                    int warnY = (SIZE - WARNING_SIZE) / 2;
                    graphics.blit(RenderPipelines.GUI_TEXTURED, WARNING_TEXTURE, x + 1, y + warnY, 0, 0, WARNING_SIZE, WARNING_SIZE, WARNING_SIZE, WARNING_SIZE);
                }

            }
        }
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    public void calculateStatusEffectIconsOffset(GuiGraphics graphics, DeltaTracker tickCounter, CallbackInfo ci, @Share("shift") LocalIntRef shiftRef) {
        ArmorHudConfig config = getManager().getConfig();
        if (!config.isEnabled() || !config.isPushStatusEffectIcons() || config.getAnchor() != ArmorHudConfig.Anchor.TOP
                || config.getSide() != ArmorHudConfig.Side.RIGHT) return;

        Player player = getCameraPlayer();
        if (player == null) return;

        Optional<Rect2i> rect = getEffectiveWidgetRect(graphics, player);
        if (rect.isEmpty()) return;

        shiftRef.set(rect.get().getY() + rect.get().getHeight());
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "STORE"), ordinal = 3)
    public int statusEffectIconsOffset(int y, @Share("shift") LocalIntRef shiftRef) {
        return y + shiftRef.get();
    }
}
