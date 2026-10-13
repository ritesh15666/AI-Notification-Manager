# Implementation Plan - Neon Smart Inbox Dashboard Redesign

Redesign the Smart Inbox screen (`InboxActivity.java`, `activity_inbox.xml`, `item_notification.xml`) to match the "Neon Smart Inbox Dashboard" reference style with deep dark navy/black background (`#080A18`), card background (`#101329`), primary violet accents (`#8057FF`), refined header with notification bell indicator, search bar, pill category filter chips, and polished notification cards featuring app icons, chevron indicators, and unread dots.

## User Review Required

> [!IMPORTANT]
> - **Neon Dark Palette**: Configure inbox-specific or theme colors for the Neon Smart Inbox dashboard (`#080A18` background, `#101329` card surface, `#8057FF` violet accent, `#F6F7FF` text, `#293866` border).
> - **Layout & Item Redesign**: Update `activity_inbox.xml` and `item_notification.xml` with rounded search inputs, pill chip filters, chevron indicators, and app icon formatting.
> - **Preservation of Logic**: All notification database querying, search filtering, category tabs, and detail navigation remain fully functional.

## Open Questions
- None. Requirements are clear.

## Proposed Changes

### Layout & UI Redesign
#### [MODIFY] [activity_inbox.xml](file:///D:/AI-Notification-Manager/app/src/main/res/layout/activity_inbox.xml)
- Refine background to deep dark navy, update header layout with bell indicator, search bar styling, and category filter chips.

#### [MODIFY] [item_notification.xml](file:///D:/AI-Notification-Manager/app/src/main/res/layout/item_notification.xml)
- Refine notification card styling with card surface `#101329`, border `#293866`, app icon (~36dp), title, message preview, category chip badge, unread indicator, and right chevron arrow.

## Verification Plan

### Automated Tests
- Run Gradle build (`app:assembleDebug`) to verify compilation and resource validity.

### Manual Verification
- Test search filtering, category chip selection, and opening notification details in Smart Inbox.
