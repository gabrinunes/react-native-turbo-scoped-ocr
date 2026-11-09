# ml-kit-camera

Módulo nativo Expo (Android) que lê texto com a câmera usando CameraX + ML Kit + Jetpack Compose, e devolve o resultado pro JS por evento.

> Status: estudo/protótipo. Só Android por enquanto.

[GIF ou vídeo curto aqui]

## Por que um módulo nativo

Libs de OCR costumam devolver "o texto que viu". Aqui o controle do pipeline (análise de frames, backpressure, liberação de cada imagem, parada da análise) fica no nativo, e a regra de negócio fica no JS.

## Stack

- Expo [versão] / React Native [versão]
- CameraX 1.4.0
- ML Kit Text Recognition 16.0.1
- Jetpack Compose

## Como rodar

Não funciona no Expo Go, porque tem código nativo. Precisa de development build:

```bash
npm install
npx expo run:android
```

Build release:

```bash
npx expo run:android --variant release
```

As pastas `android/` e `ios/` da raiz são geradas pelo prebuild e não são versionadas.

## Uso

```tsx
import { MlKitCameraView } from './modules/ml-kit-camera';

<MlKitCameraView
  onTextRecognized={(e) => console.log(e.nativeEvent.text)}
/>
```

| Prop | Tipo | Descrição |
|---|---|---|
| `onTextRecognized` | `(e) => void` | Dispara com `e.nativeEvent.text` |

## Permissões

A permissão `CAMERA` está declarada no manifest do módulo. Em runtime, o app ainda precisa pedir a permissão antes de montar a view.

## Release e R8

Com a minificação ligada, o R8 quebrava o `TextRecognition.getClient` do ML Kit (view com tela preta no release, debug normal). As regras de keep estão em `modules/ml-kit-camera/android/consumer-rules.pro` e acompanham o módulo.

## Limitações

- Só Android.
- [Testado em quais aparelhos?]
- [Qualquer limite que você conheça, por exemplo a precisão do OCR em movimento]

## Estrutura

```
modules/ml-kit-camera/
  android/          Kotlin, build.gradle, consumer-rules.pro
  src/              TSX do componente
  expo-module.config.json
```
