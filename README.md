# master-detail-flow-revised
Revision of base Android Studio Master/Detail Flow activity template to resemble Master/Detail flow from GMail and other applications.

## Setup

1. Setup [JitPack](https://jitpack.io/) repository use in your project
2. Download template/MasterDetailFlowRevised
3. Copy MasterDetailFlowRevised to {Android Studio installation directory}\plugins\android\lib\templates\activities
4. Create new Activity from Android Studio Activity Gallery

## Description

The problem I have with base Master/Detail Flow template is that it works ambiguously on (some?) tablets. When in portrait mode, it works as on smartphones - list and details are displayed separately. When in landscape mode, list and details are displayed simultaneously, side by side. The problem occurs when item is selected and orientation changes.
When orientation is changed from portrait (details as separate Activity) to landscape, details layout is only expanded to landcape. I expect that after orientation change, the list would be displayed at the side with current item selected.
When orientation is changed from landscape (list and details in same Activity) to portrait, details disappear and only list is displayed. I expect that details of item selected prior to orientation change would be displayed.
My expectations come from the fact that this is how, for example, GMail works.
Template works OK on smartphones (list and details are always displayed separately because there is not space to display them both) and possibly on some bigger tablets (list and details are always displayed togehter, because there is always space to do this, regardless the orientation), however, on some smaller tablets (e.g. 8.5") it works as decribed above, which is not shiny at all (at least in my opinion).
In this repository, I am trying to fix this template to work correcly on all kind of screens.
