![Server Sided Portals banner](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/server-sided-portals/banner.png "Server Sided Portals banner")

---

![Minecraft](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/minecraft.svg "Minecraft")[![1.21.4](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-21-4.svg "1.21.4")](https://www.patreon.com/crystalspider/membership)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![1.21.3](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-21-3.svg "1.21.3")](https://modrinth.com/mod/server-sided-portals/versions?g=1.21.3)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![1.21.1](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-21-1.svg "1.21.1")](https://modrinth.com/mod/server-sided-portals/versions?g=1.21.1)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![1.21](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-21.svg "1.21")](https://modrinth.com/mod/server-sided-portals/versions?g=1.21)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![1.20.4](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-20-4.svg "1.20.4")](https://modrinth.com/mod/server-sided-portals/versions?g=1.20.4)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![1.20.2](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-20-2.svg "1.20.2")](https://modrinth.com/mod/server-sided-portals/versions?g=1.20.2)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![1.20.1](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-20-1.svg "1.20.1")](https://modrinth.com/mod/server-sided-portals/versions?g=1.20.1)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![1.19.4](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-19-4.svg "1.19.4")](https://modrinth.com/mod/server-sided-portals/versions?g=1.19.4)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![1.19.2](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/minecraft/1-19-2.svg "1.19.2")](https://modrinth.com/mod/server-sided-portals/versions?g=1.19.2)

![Loader](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/loader/loader.svg "Loader")[![NeoForge](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/loader/neoforge.svg "NeoForge")](https://modrinth.com/mod/server-sided-portals/versions?l=neoforge)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![Forge](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/loader/forge.svg "Forge")](https://modrinth.com/mod/server-sided-portals/versions?l=forge)![Separator](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/separator.svg)[![Fabric](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/loader/fabric.svg "Fabric")](https://modrinth.com/mod/server-sided-portals/versions?l=fabric)

![Overlay](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/side/server.svg)

![Issues](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/github/issues.svg "Issues")[![GitHub](https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/github/github.svg "GitHub")](https://github.com/crystal-nest/server-sided-portals/issues)

---

## **Description**

This is a mod API that provides easily customizable and server-sided portals to any custom dimension.  
This mod is required server side only, and is thus compatible with any client, Vanilla included.  
This mod can be used as a dependency for another mod/datapack, or bundled in a modpack with a custom datapack or with mods that require it.

Check out the usage details below or on the [Wiki](https://github.com/Crystal-Nest/server-sided-portals/wiki)!

## **Features**

- Allows the creation of portals with a custom frame that are linked for a specific dimension.
- Choose the frame blocks simply with a block tag! Multiple blocks are allowed!
- Required only server side!
- Fully compatible with both modded and Vanilla clients.
- Easy to use with either a mod or a datapack.

## **Usage**

You can find up-to-date examples for both a datapack and a mod on the [Wiki](https://github.com/Crystal-Nest/server-sided-portals/wiki/Working-examples).

### Datapack

It's very easy to make use of this mod API with a custom datapack:

1. Create a datapack following [this tutorial](https://minecraft.wiki/w/Tutorials/Creating_a_data_pack).
2. Create a custom dimension type with [this generator](https://misode.github.io/dimension-type/).
3. Create a custom dimension with [this generator](https://misode.github.io/dimension/).
4. Create a custom block tag for the portal frame. You can add multiple blocks and other block tags too!
5. Make sure all the JSON files you created in the previous steps are called the exact same.

That's it! When the datapack is loaded along with this mod, all dimensions will be loaded, and it'll be possible to create portals with the specified frame blocks!  
You can also add multiple dimensions, each with its custom portal definition: just make sure that, for each one, the dimension files and the block tag file are called the same.

### Mod

Making a mod that leverages this API is simple.  
Follow the instructions in the datapack section above, but put the files under `resources/data/mod_id/`.  
Making a mod rather than a datapack might be useful for adding extra functionality regarding your dimension.

There are also a few useful utility methods available, for which you can check out the Javadoc for more details.

## **Compatibilities**

| Mod                                                            | Loader |                                                         Compatibility                                                          |
|:---------------------------------------------------------------|:------:|:------------------------------------------------------------------------------------------------------------------------------:|
| [Crying Portals](https://modrinth.com/mod/crying-portals)      |  All   |                                                          Incompatible                                                          |
| [Immersive Portals](https://modrinth.com/mod/immersiveportals) |  All   |                                                          Incompatible                                                          |
| [BetterNether](https://modrinth.com/mod/betternether)          | Fabric |                                                          Incompatible                                                          |
| [Very Many Players](https://modrinth.com/mod/vmp-fabric)       | Fabric |                                           Compatible with `use_async_portals=false`                                            |
| [Canary](https://modrinth.com/mod/canary)                      | Forge  | Compatible with [fast portals](https://github.com/AbdElAziz333/Canary/wiki/Configuration-File#mixinaipoifast_portals) disabled |

## **Dependencies**

| Mod                                       | Loader | Requirement |
|:------------------------------------------|:------:|:-----------:|
| [Cobweb](https://modrinth.com/mod/cobweb) |  All   |  Required   |

## **License and right of use**

Feel free to use this mod for any modpack or video, just be sure to give credit and possibly link [here](https://github.com/crystal-nest/server-sided-portals#readme).  
This project is published under the [GNU General Public License v3.0](https://github.com/crystal-nest/server-sided-portals/blob/master/LICENSE).

## **Support us**

<a href="https://crystalnest.it"><img alt="Crystal Nest Website" src="https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/crystal-nest/pic512.png" width="14.286%"></a><a href="https://discord.gg/BP6EdBfAmt"><img alt="Discord" src="https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/discord/discord512.png" width="14.286%"></a><a href="https://www.patreon.com/crystalspider"><img alt="Patreon" src="https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/patreon/patreon512.png" width="14.286%"></a><a href="https://ko-fi.com/crystalspider"><img alt="Ko-fi" src="https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/kofi/kofi512.png" width="14.286%"></a><a href="https://github.com/Crystal-Nest"><img alt="Our other projects" src="https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/github/github512.png" width="14.286%"><a href="https://modrinth.com/organization/crystal-nest"><img alt="Modrinth" src="https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/modrinth/modrinth512.png" width="14.286%"></a><a href="https://www.curseforge.com/members/crystalspider/projects"><img alt="CurseForge" src="https://raw.githubusercontent.com/crystal-nest/mod-fancy-assets/main/curseforge/curseforge512.png" width="14.286%"></a>

[![Bisect Hosting](https://www.bisecthosting.com/partners/custom-banners/d559b544-474c-4109-b861-1b2e6ca6026a.webp "Bisect Hosting")](https://bisecthosting.com/crystalspider)
