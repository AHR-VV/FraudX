import backendHandoffMd from '../../BACKEND_HANDOFF.md?raw';
import settingsGradleKts from '../../settings.gradle.kts?raw';
import rootBuildGradleKts from '../../build.gradle.kts?raw';
import appBuildGradleKts from '../../app/build.gradle.kts?raw';
import androidManifestXml from '../../app/src/main/AndroidManifest.xml?raw';
import shieldVectorXml from '../../app/src/main/res/drawable/ic_fraudx_shield.xml?raw';
import mainActivityKt from '../../app/src/main/java/com/fraudx/app/MainActivity.kt?raw';
import colorKt from '../../app/src/main/java/com/fraudx/app/theme/Color.kt?raw';
import typeKt from '../../app/src/main/java/com/fraudx/app/theme/Type.kt?raw';
import themeKt from '../../app/src/main/java/com/fraudx/app/theme/Theme.kt?raw';
import modelsKt from '../../app/src/main/java/com/fraudx/app/model/FraudXModels.kt?raw';
import repositoryKt from '../../app/src/main/java/com/fraudx/app/data/FraudXRepository.kt?raw';
import mockRepositoryKt from '../../app/src/main/java/com/fraudx/app/data/MockFraudXRepository.kt?raw';
import viewModelKt from '../../app/src/main/java/com/fraudx/app/viewmodel/FraudXViewModel.kt?raw';
import navHostKt from '../../app/src/main/java/com/fraudx/app/ui/navigation/FraudXNavHost.kt?raw';
import topBarKt from '../../app/src/main/java/com/fraudx/app/ui/components/FraudXTopBar.kt?raw';
import switchKt from '../../app/src/main/java/com/fraudx/app/ui/components/FraudXSwitch.kt?raw';
import inspectorSheetKt from '../../app/src/main/java/com/fraudx/app/ui/components/LinkInspectorSheet.kt?raw';
import auditSheetKt from '../../app/src/main/java/com/fraudx/app/ui/components/AuditLogSheet.kt?raw';
import dashboardScreenKt from '../../app/src/main/java/com/fraudx/app/ui/screens/DashboardScreen.kt?raw';
import settingsScreenKt from '../../app/src/main/java/com/fraudx/app/ui/screens/SettingsScreen.kt?raw';
import allowBlockScreenKt from '../../app/src/main/java/com/fraudx/app/ui/screens/AllowBlockScreen.kt?raw';
import interceptionLogScreenKt from '../../app/src/main/java/com/fraudx/app/ui/screens/InterceptionLogScreen.kt?raw';

export interface ProjectSourceFile {
  path: string;
  label: string;
  group: 'Screens' | 'Components & Nav' | 'State & Backend Hooks' | 'Theme & Config';
  language: 'kotlin' | 'xml' | 'gradle' | 'markdown';
  content: string;
}

export const BACKEND_HANDOFF_DOC = backendHandoffMd;

export const KOTLIN_PROJECT_FILES: ProjectSourceFile[] = [
  {
    path: 'app/src/main/java/com/fraudx/app/ui/screens/DashboardScreen.kt',
    label: 'DashboardScreen.kt',
    group: 'Screens',
    language: 'kotlin',
    content: dashboardScreenKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/ui/screens/SettingsScreen.kt',
    label: 'SettingsScreen.kt',
    group: 'Screens',
    language: 'kotlin',
    content: settingsScreenKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/ui/screens/AllowBlockScreen.kt',
    label: 'AllowBlockScreen.kt',
    group: 'Screens',
    language: 'kotlin',
    content: allowBlockScreenKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/ui/screens/InterceptionLogScreen.kt',
    label: 'InterceptionLogScreen.kt',
    group: 'Screens',
    language: 'kotlin',
    content: interceptionLogScreenKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/ui/navigation/FraudXNavHost.kt',
    label: 'FraudXNavHost.kt',
    group: 'Components & Nav',
    language: 'kotlin',
    content: navHostKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/ui/components/FraudXTopBar.kt',
    label: 'FraudXTopBar.kt',
    group: 'Components & Nav',
    language: 'kotlin',
    content: topBarKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/ui/components/FraudXSwitch.kt',
    label: 'FraudXSwitch.kt',
    group: 'Components & Nav',
    language: 'kotlin',
    content: switchKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/ui/components/LinkInspectorSheet.kt',
    label: 'LinkInspectorSheet.kt',
    group: 'Components & Nav',
    language: 'kotlin',
    content: inspectorSheetKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/ui/components/AuditLogSheet.kt',
    label: 'AuditLogSheet.kt',
    group: 'Components & Nav',
    language: 'kotlin',
    content: auditSheetKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/data/FraudXRepository.kt',
    label: 'FraudXRepository.kt',
    group: 'State & Backend Hooks',
    language: 'kotlin',
    content: repositoryKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/data/MockFraudXRepository.kt',
    label: 'MockFraudXRepository.kt',
    group: 'State & Backend Hooks',
    language: 'kotlin',
    content: mockRepositoryKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/model/FraudXModels.kt',
    label: 'FraudXModels.kt',
    group: 'State & Backend Hooks',
    language: 'kotlin',
    content: modelsKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/viewmodel/FraudXViewModel.kt',
    label: 'FraudXViewModel.kt',
    group: 'State & Backend Hooks',
    language: 'kotlin',
    content: viewModelKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/MainActivity.kt',
    label: 'MainActivity.kt',
    group: 'Theme & Config',
    language: 'kotlin',
    content: mainActivityKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/theme/Color.kt',
    label: 'Color.kt',
    group: 'Theme & Config',
    language: 'kotlin',
    content: colorKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/theme/Type.kt',
    label: 'Type.kt',
    group: 'Theme & Config',
    language: 'kotlin',
    content: typeKt,
  },
  {
    path: 'app/src/main/java/com/fraudx/app/theme/Theme.kt',
    label: 'Theme.kt',
    group: 'Theme & Config',
    language: 'kotlin',
    content: themeKt,
  },
  {
    path: 'app/build.gradle.kts',
    label: 'app/build.gradle.kts',
    group: 'Theme & Config',
    language: 'gradle',
    content: appBuildGradleKts,
  },
  {
    path: 'build.gradle.kts',
    label: 'build.gradle.kts',
    group: 'Theme & Config',
    language: 'gradle',
    content: rootBuildGradleKts,
  },
  {
    path: 'settings.gradle.kts',
    label: 'settings.gradle.kts',
    group: 'Theme & Config',
    language: 'gradle',
    content: settingsGradleKts,
  },
  {
    path: 'app/src/main/AndroidManifest.xml',
    label: 'AndroidManifest.xml',
    group: 'Theme & Config',
    language: 'xml',
    content: androidManifestXml,
  },
  {
    path: 'app/src/main/res/drawable/ic_fraudx_shield.xml',
    label: 'ic_fraudx_shield.xml',
    group: 'Theme & Config',
    language: 'xml',
    content: shieldVectorXml,
  },
];
