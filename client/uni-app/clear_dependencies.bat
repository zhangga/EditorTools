@ECHO OFF

@REM # �����Ŀ��Ŀ¼���ⲿ������Ŀ¼
@REM #-------------------------------------------------------------------------------------


IF EXIST package-lock.json (

    ECHO ɾ��package-lock.json��...
    ECHO ----------------------------------------------------------------------
    DEL package-lock.json
)

IF NOT EXIST dependencies.txt (

    ECHO �Ҳ���dependencies.txt�ļ���������
    PAUSE

) ELSE (

    ECHO ɾ����Ŀ¼��������Ŀ¼��...
    ECHO ----------------------------------------------------------------------

    FOR /F "tokens=*" %%L in (dependencies.txt) DO (
        IF EXIST "%%L" (
            RD /S /Q "%%L"
        )
    )

    DEL dependencies.txt
)

ECHO ----------------------------------------------------------------------
@if "%1" == "1" PAUSE