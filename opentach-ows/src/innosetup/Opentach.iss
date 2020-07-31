#define APP_NAME "Opentach" 
#define TARGET_EXE_FILE APP_NAME+".exe"
#define TARGET_FILE_NAME APP_NAME+"Setup"
#define TARGET_GROUP APP_NAME
#define TARGET_OAD APP_NAME+".oad"
#define JVM_PATH "e:\deployments\open_jdk8u222-b10-jre"
#define APPLICATION_BASE_PATH "e:\deployments\OpentachRelease\opentach-ows"
#define EXE_FILE APPLICATION_BASE_PATH+"\target\"+ TARGET_EXE_FILE
#define OAD_FILE APPLICATION_BASE_PATH+"\target\extra-resources\"+TARGET_OAD

[Setup]
AppName={#TARGET_GROUP}
AppVersion=1.0.0.0  
VersionInfoVersion=1.0.0.0
DefaultDirName={userappdata}\{#APP_NAME}
DefaultGroupName={#TARGET_GROUP}
OutputBaseFilename={#TARGET_FILE_NAME}
PrivilegesRequired=lowest
AppPublisher=Imatia Innovation S.L.
AppPublisherURL=https://www.imatia.com

[Files]
Source: "{#EXE_FILE}"; DestDir: "{app}"
Source: "{#OAD_FILE}"; DestDir: "{app}"
Source: "{#JVM_PATH}\*"; DestDir: "{app}\jvm"; Flags: ignoreversion recursesubdirs

[Icons]
Name: "{group}\{#TARGET_GROUP}"; Filename: "{app}\{#TARGET_EXE_FILE}"
Name: "{userdesktop}\{#APP_NAME}"; Filename: "{app}\{#TARGET_EXE_FILE}"; WorkingDir: "{app}" 

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"