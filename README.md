# Flooded Graveyard

This is an atmospheric 3D scene depicting a flooded graveyard at night, with trees, grave markers, and a spooky ghost that drifts aimlessly in search of lost souls. The sky is cloudy with patches of stars showing through.

This was my final project for the course CSC 155 Advanced Computer Graphics taught by [Dr. Scott Gordon](https://athena.ecs.csus.edu/~gordonvs/) at [CSU Sacramento](https://www.csus.edu/). I created it with Java, OpenGL, and GLSL. It uses [JOGL](https://jogamp.org/jogl/www/) for OpenGL bindings and [JOML](https://github.com/JOML-CI/JOML) for vector/matrix operations.

## Controls

| Control | Function |
| --- | --- |
| W/A/S/D/Q/E | translate camera |
| shift | increase camera speed |
| ←/→/↑/↓ | camera pan/pitch |
| spacebar | toggle world axes |
| F1 | reset camera position/orientation |
| F2 | toggle yellow point light marker |
| F3 | toggle point light |
| F4 | toggle water |
| mouse wheel | move point light along y axis |
| drag mouse | move point light parallel to the x-z plane (relative to camera). The further the mouse is from the initial click point, the faster the light moves. |

## Features

- Camera controller
- Blinn-Phong lighting
- Normal mapping
- Omnidirectional shadows (single light source)
- Skybox
- Fog
- Animated water (above/below surface)
- .obj model importer with
    - partial .mtl support
    - automatic tangent generation
- Multiple textures per model
- Cutouts/alpha blending
- 3D Perlin noise texture

## Credits

- [Pine tree model and texture created by peter19770301](https://www.cgtrader.com/free-3d-models/plant/conifer/pine-tree-model), released under cgtrader’s royalty free license.
- [Grave marker models created by Lamoot](https://opengameart.org/content/rts-medieval-props) and [unwrapped by Clint Bellanger](https://opengameart.org/content/medieval-props-textured), released under the CC-BY 3.0 license.
- [Small gravestone texture/normal map created by Joao Paulo](https://3dtextures.me/2019/01/03/rock-028/), released under the CC0 1.0 license.
- [Ghost model created by Timmber](https://www.cgtrader.com/free-3d-models/character/clothing/3-black-hooded-capes), released under cgtrader’s royalty free license.
- [Skybox adapted from one created by Spiney](https://opengameart.org/content/cloudy-skyboxes), released under the CC-BY 3.0 license. Stars added by me using Photoshop.
- [Ground texture created by Cethiel](https://opengameart.org/content/tileable-grass-textures-set-2), released under CC0 1.0 license (public domain). Normal map created by me using Photoshop.
