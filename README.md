# NetamiReportSystem

A comprehensive report system plugin for Minecraft servers that allows players to report issues and administrators to manage them efficiently.

## Features

- Player reporting system with multiple report types (User, Bug, Map Bug)
- Admin panel with GUI management
- Report assignment and tracking
- Location-based reporting with teleportation
- Statistics and report history
- Admin notifications

## Requirements

- Java 8 or higher
- Maven 3.6 or higher
- Spigot/Paper 1.16+ server

## Compilation

1. Navigate to the plugin directory:
```bash
cd NetamiReportSystem
```

2. Compile with Maven:
```bash
mvn clean compile
```

3. Build the plugin JAR:
```bash
mvn clean package
```

The compiled plugin will be located in `target/netami-report-system-1.0.jar`

## Installation

1. Copy the JAR file to your server's `plugins` folder
2. Restart your server
3. Configure permissions as needed

## Commands

### Player Commands
- `/report` - Open the report creation menu
- `/report <player>` - Report a specific player
- `/myreports` - View your submitted reports

### Admin Commands
- `/reports` - Open the admin panel
- `/reports reload` - Reload the plugin configuration

## Permissions

### Player Permissions
- `reports.use` - Allow players to create reports
- `reports.view` - Allow players to view their own reports

### Admin Permissions
- `reports.admin` - Access to admin panel and report management
- `reports.notify` - Receive notifications about report updates
- `reports.teleport` - Teleport to report locations
- `reports.assign` - Assign reports to administrators
- `reports.close` - Close and resolve reports
- `reports.reset` - Reset all reports (dangerous)

## Usage

### For Players
1. Use `/report` to open the report menu
2. Select the type of report (User, Bug, Map Bug)
3. Follow the prompts to provide details
4. Submit the report

### For Administrators
1. Use `/reports` to open the admin panel
2. Browse reports by category or status
3. Click on reports to view details
4. Assign, close, or resolve reports as needed
5. Use the teleport feature to visit report locations

## Configuration

The plugin creates a configuration file in `plugins/NetamiReportSystem/config.yml` where you can customize:
- Database settings
- Message templates
- Report categories
- Admin notification settings

## Database

The plugin uses SQLite by default and creates a database file at `plugins/NetamiReportSystem/reports.db`. All reports and statistics are stored locally.

## Support

For issues or questions, check the server logs for error messages and ensure all permissions are configured correctly.