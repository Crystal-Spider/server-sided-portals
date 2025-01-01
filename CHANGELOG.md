# Change Log

All notable changes to the "server-sided-portals" Minecraft mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Crystal Nest Semantic Versioning](https://crystalnest.it/#/versioning).

## [Unreleased]

- Port to 1.21.4.

## [v2.0.0] - 2025/01/01

- 1.21 and above only.
- Using `server_sided_portals` namespace in the datapack definitions (dimensions and tags) is now not required anymore and advised against.
- Dimension definitions can now be located under any namespace, thus allowing to support already existing dimensions (e.g. added by other mods).
- The block tag for a dimension's portal frame now needs to be named `dimension_portal_frame` (where `dimension` is the dimension name) and located under the same namespace as the dimension it is for.
- Added new item tag for a dimension's portal igniter. Unlike the frame block tag, this tag is not mandatory.  
  However, similarly to the frame block tag, it needs to be named `dimension_portal_igniter` (where `dimension` is the dimension name) and located under the same namespace as the dimension it is for.  
  If the tag is absent or empty, portals light up with fire (as normal Nether portals). If the tag is present and non-empty, portals light up **only** with the items specified in the tag and fire won't work anymore.
- Improved mod compatibility by tweaking mixins.
- Added compatibility with `BetterNether` mod (1.21 and 1.21.1 only, as `BetterNether` is not available for later versions at the time of writing).

## [v1.1.1] - 2024/12/16

- Ported to 1.21.3.

## [v1.1.1] - 2024/09/30

- Added support for 1.21.1.

## [v1.1.0] - 2024/09/15

- Added new utility method overload `isPortalForDimension(Level, BlockPos, String)`.
- 1.20.1 only, fixed NeoForge build.

## [v1.0.0] - 2024/09/14

- Added custom dimension travel with custom portals.
- Exposed utility API.

[Unreleased]: https://github.com/crystal-nest/server-sided-portals
[README]: https://github.com/crystal-nest/server-sided-portals#readme

[v2.0.0]: https://github.com/crystal-nest/server-sided-portals/releases?q=2.0.0
[v1.1.1]: https://github.com/crystal-nest/server-sided-portals/releases?q=1.1.1
[v1.1.0]: https://github.com/crystal-nest/server-sided-portals/releases?q=1.1.0
[v1.0.0]: https://github.com/crystal-nest/server-sided-portals/releases?q=1.0.0
