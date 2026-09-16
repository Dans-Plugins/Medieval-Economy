# What is this?
This Minecraft plugin provides a virtual coinpurse and a physical currency item for the purpose of simulating an economy.

## Usage reporting

Usage reporting is on by default: when the plugin is enabled, and each time one of its commands is used, it sends its name, version and the command's name (`startup` and `command` events) to https://trace.danielstephenson.dev so it is known which plugins are actually in use. Nothing about players, worlds, IPs or the server is sent, and nothing typed after a command. The plugin says on every startup whether reporting is on. To turn it off:

- `usage-reporting.enabled: false` in this plugin's `config.yml`
- for every plugin on the server that reports to trace: `enabled: false` in `plugins/trace/config.yml` (written by the first such plugin to start)
- the environment variable `TRACE_USAGE_REPORTING=off` or `DO_NOT_TRACK=1`

Details: https://github.com/Stephenson-Software/trace#usage-reporting
