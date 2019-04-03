@ECHO OFF

@REM # ��package.json�ָ�npm repo
@REM #-------------------------------------------------------------------------------------


IF NOT EXIST package.json (
    ECHO �Ҳ���package.json�ļ���������
    pause
)

IF EXIST node_modules (

    ECHO ɾ����ǰ���ڵ�node_modules��...
    ECHO ----------------------------------------------------------------------
    RD /S /Q node_modules
)

IF EXIST package-lock.json (

    ECHO ɾ��package-lock.json��...
    ECHO ----------------------------------------------------------------------
    DEL package-lock.json
)

IF EXIST dependencies.txt (

    ECHO ��ո�Ŀ¼�������ļ���...
    ECHO ----------------------------------------------------------------------
    for /F "tokens=*" %%L in (dependencies.txt) DO (
        IF EXIST "%%L" (
            RD /S /Q "%%L"
        )
    )


    ECHO ���dependencies.txt��...
    ECHO ----------------------------------------------------------------------
    DEL dependencies.txt
)

ECHO ��ʼ��ʼ��npm repository...
ECHO ----------------------------------------------------------------------
CALL npm install

ECHO ����dependencies.txt��¼������������
ECHO ----------------------------------------------------------------------
break > dependencies.txt

IF EXIST node_modules (

    for /D %%F in (node_modules/*) DO (
         @ECHO %%F>> dependencies.txt
    )

    ECHO ��������������Ŀ��Ŀ¼��...
    ECHO ----------------------------------------------------------------------
    xcopy node_modules\* .\* /I /E /y /q

    RD /S /Q node_modules
)

ECHO ----------------------------------------------------------------------
@if "%1" == "1" PAUSE
