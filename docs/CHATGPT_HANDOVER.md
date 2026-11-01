# Handover prompt for ChatGPT: UI art for Cobblemon - Battleplacks

Paste the prompt below into ChatGPT and attach the images listed under "Attach these".

## Attach these

1. **Your reference screenshot** (the server HUD: Pokemon tile on top, trainer bust and Pokemon cropped underneath, thin party bar, "Team <name>" text).
2. **A screenshot of the current mod in-game** (so ChatGPT sees what needs to look better).
3. **Cobblemon's own battle UI textures** as style references. Extract them from the Cobblemon jar (they are references only, do not ship them in this repo):

```bash
mkdir -p ~/cobblemon-ui-refs && cd ~/cobblemon-ui-refs
unzip -j "/path/to/Cobblemon-fabric-1.8.4+1.21.1.jar" \
  'assets/cobblemon/textures/gui/battle/battle_info_base.png' \
  'assets/cobblemon/textures/gui/battle/battle_info_base_flipped.png' \
  'assets/cobblemon/textures/gui/battle/battle_info_role.png' \
  'assets/cobblemon/textures/gui/battle/battle_info_underlay.png' \
  'assets/cobblemon/textures/gui/battle/battle_status_*.png'
```

Sizes (GUI pixels): `battle_info_base.png` 140x40 (grey metal frame, square portrait window on the left, long bar to the right), `battle_info_role.png` 27x12, `battle_info_underlay.png` 28x28, status icons about 74x7.

## The prompt

```text
You are a pixel-art UI artist helping with a Minecraft 1.21.1 mod called "Cobblemon - Battleplacks"
(an addon for the Cobblemon mod). I need you to design texture art for a small HUD element.

WHAT THE HUD IS
During a Cobblemon battle the mod draws a compact "strip" directly underneath Cobblemon's own
Pokemon tile, in the top-left corner (mine) and top-right corner (opponent's, mirrored).
The strip shows, from top to bottom:
  1. A 140x44 box. Inside it the trainer's 3D model (head at the top of the box, body running down
     until the box cuts it off at the bottom edge) and the active Pokemon's 3D model next to the
     trainer, also aligned to the top and cut off at the bottom. Neither is shown full length.
  2. A thin segmented party bar, 140x8, one segment per Pokemon (up to 6): coloured when healthy,
     red-dark when fainted, grey when unknown.
  3. The text "Team <name>" in white Minecraft font with a drop shadow.
The 3D models and all text are drawn by the mod in code. Do NOT bake any character, Pokemon or text
into the textures.

STYLE
Match Cobblemon's own battle tile (reference images attached): grey brushed-metal frame, 1px dark
outline, thin light bevel on the top and left edges, darker inset panel, small rivets or notches are
fine. Slightly desaturated and clean, readable on top of any Minecraft world (grass, snow, caves).
The inner panel must be semi-transparent dark (alpha about 150 to 180 out of 255) so the world shows
faintly through and the 3D models stay readable. The look should feel like part of the same UI family
as the attached Cobblemon tile, not a different game.

DELIVERABLES (all PNG, RGBA, true pixel art with hard edges, no anti-aliasing, no blur)
  A. strip.png          exactly 140 x 44 px   background frame for the model box (see above).
                        Must work for BOTH the left and right corner, so keep it left/right symmetric.
  B. party_bar.png      exactly 140 x 8 px    thin frame for the party bar. Leave the inside empty
                        and dark so the mod can draw coloured segments over it.
  C. (optional) intro_banner.png  exactly 140 x 20 px  a nameplate frame for a short battle intro.
Also give each as a 4x master (560x176, 560x32, 560x80) made with nearest-neighbour scaling so I can
check details, but the files I will use are the exact sizes above.

CONSTRAINTS
  - Exact pixel sizes matter; the mod blits the textures 1:1 in GUI pixels.
  - Transparent corners are allowed (rounded or chamfered corners look good).
  - No text, no logos, no watermarks, nothing copyrighted that is not already part of the style
    reference.
  - Do not copy Cobblemon's textures pixel for pixel; draw something original in the same style.

HOW THE FILES ARE USED
  common/src/main/resources/assets/battleplacks/textures/gui/strip.png
  A resource pack can override them at assets/battleplacks/textures/gui/strip.png.
Please also tell me, for each file, the main colours as hex codes so I can tweak them.

First, describe in 4 to 6 bullets how you interpret the style from the reference images, then
produce the files. If you cannot output real PNG files, give me a precise pixel-grid description
or a small Python (Pillow) script that generates them at the exact sizes.
```
