---
layout: default
title: Retention Policies
nav_order: 4
has_children: true
has_toc: true
---

# Retention Policies

To reduce storage fill up, it is possible to set a retention policy per view.
A retention policy has to be added together with its view.

# Deletion Policies

When a member is removed from its last view, they are no longer automatically removed from the event stream.


## Retention polling interval
By default, every day, the server checks if there are members that can be deleted that are not conform to the retention policy anymore.
If a higher retention accuracy is desired, or a lower one if resources are limited for example, then a respectively lower or higher retention polling interval can be set via a cron expression.

To configure this interval, please refer to the [Configuration Page.](../../how-to-run#ldes-server-config)

## Supported Retention Policies:
