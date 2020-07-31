#define APP_NAME "SimpleDownloader" 
#define TARGET_EXE_FILE APP_NAME+".exe"
#define TARGET_FILE_NAME APP_NAME+"Setup"
#define TARGET_GROUP APP_NAME
#define JVM_PATH "F:\PROJECTS\WORKSPACE_TACOGRAFOS\opentach\opentach-client-simpledownloader\target\jvm"
#define APPLICATION_BASE_PATH "F:\PROJECTS\WORKSPACE_TACOGRAFOS\opentach\opentach-client-simpledownloader"
#define EXE_FILE APPLICATION_BASE_PATH+"\target\"+ TARGET_EXE_FILE

[Setup]
AppName={#TARGET_GROUP}
AppVersion=1.0.0.0  
VersionInfoVersion=1.0.0.0
DefaultDirName={userappdata}\{#APP_NAME}
DefaultGroupName={#TARGET_GROUP}
OutputBaseFilename={#TARGET_FILE_NAME}
PrivilegesRequired=lowest
AppPublisher=Openservices Consultoría en Transportes
AppPublisherURL=http://www.opentach.com

[Files]
Source: "{#EXE_FILE}"; DestDir: "{app}"
Source: "{#JVM_PATH}\*"; DestDir: "{app}\jvm"; Flags: ignoreversion recursesubdirs

[Icons]
Name: "{group}\{#TARGET_GROUP}"; Filename: "{app}\{#TARGET_EXE_FILE}"
Name: "{userdesktop}\{#APP_NAME}"; Filename: "{app}\{#TARGET_EXE_FILE}"; WorkingDir: "{app}" 

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"