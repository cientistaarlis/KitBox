# Meu Estoque de Componentes Eletrônicos

Aplicativo Android desenvolvido com **Kotlin e Jetpack Compose** para cadastrar, consultar, editar e excluir componentes eletrônicos usados em casa ou em um laboratório. O projeto foi estruturado para demonstrar os principais conceitos de estado, navegação, persistência local, ciclo de vida, efeitos colaterais, listas e câmera.

## Funcionalidades

O aplicativo inicia com três registros de exemplo e permite cadastrar resistores, capacitores, sensores, microcontroladores e outros componentes. Cada registro possui nome, categoria, encapsulamento, quantidade, estoque mínimo, observações e marcação de favorito. Os registros podem ser filtrados por texto, categoria, favoritos e estoque baixo.

A tela inicial usa cartões com indicador de estoque e disponibiliza ações de detalhe, edição, exclusão e alteração de favorito. A tela de detalhes recebe o identificador do registro pela rota de navegação. O formulário é reutilizado para cadastro e edição.

A tela de câmera é aberta em uma **Activity separada** por meio de `Intent`. Ela utiliza CameraX para pré-visualização, captura de fotografias e gravação de vídeos. O analisador de imagem usa ML Kit Barcode Scanning para detectar códigos de barras e QR Codes.

## Requisitos demonstrados

| Conceito | Implementação no projeto |
|---|---|
| `TextField` | Busca na `HomeScreen` e campos do formulário em `ComponentFormScreen`. |
| `RadioButton` | Seleção de categoria no formulário. |
| `Switch` | Filtro de favoritos e marcação do componente como favorito. |
| `Slider` | Estoque mínimo no formulário e filtro visual de estoque baixo na Home. |
| `Column` e `Spacer` | Organização vertical de telas, cartões e seções. |
| `NavHost` e rotas | Rotas `home`, `form?componentId={componentId}` e `detail/{componentId}` em `MainActivity`. |
| Controle de estado na navegação | `rememberNavController`, `popBackStack` e leitura de `componentId` via `NavBackStackEntry`. |
| `remember` | Lista filtrada calculada em `HomeScreen` sem trabalho desnecessário a cada recomposição. |
| `rememberSaveable` | Busca, filtros, campos do formulário, favorito, estado de gravação e código detectado. |
| Recomposition | Alterações de estado atualizam cards, contadores, filtros e validações sem recriar a Activity. |
| Ciclo de vida | `MainActivity` registra `onCreate`, `onStart`, `onResume`, `onPause`, `onStop` e `onDestroy` no `InventoryViewModel`. |
| `LaunchedEffect` | Exibição de Toast de validação fora da composição visual. |
| `DisposableEffect` | Vinculação e liberação de CameraX e do analisador ML Kit conforme o ciclo da tela. |
| `Activity` + Compose | A UI é declarada em `setContent`; as Activities continuam controlando o ciclo de vida. |
| `Intent` | `MainActivity` abre `CameraActivity` e a câmera retorna ao estoque com `finish()`. |
| `LazyColumn` | Lista principal de componentes. |
| `LazyRow` | Filtros horizontais por categoria. |
| `LazyHorizontalGrid` | Resumo visual por categoria em grade. |
| DataStore | Lista de componentes serializada em JSON dentro de `Preferences DataStore`. |
| CameraX + ML Kit | Captura de foto, vídeo e leitura de código na `CameraScreen`. |

## Estrutura do projeto

```text
app/src/main/java/com/example/meuestoquedecomponentes/
├── MainActivity.kt                 # NavHost, rotas e ciclo de vida
├── CameraActivity.kt               # Activity separada para a câmera
├── data/
│   ├── Component.kt                 # Modelo e categorias
│   └── InventoryRepository.kt      # Persistência com DataStore
├── screens/
│   ├── HomeScreen.kt                # Busca, filtros, listas e CRUD visual
│   ├── ComponentFormScreen.kt       # Cadastro e edição
│   ├── ComponentDetailScreen.kt     # Detalhamento por parâmetro de rota
│   └── CameraScreen.kt              # CameraX, vídeo e ML Kit
└── ui/
    ├── InventoryViewModel.kt        # Estado e operações do domínio
    └── Theme.kt                     # Tema Material 3
```

## Como executar

Abra a pasta `meu-estoque-componentes` no Android Studio atualizado. Aguarde a sincronização do Gradle e execute a configuração `app` em um emulador ou dispositivo com Android 8.0 (API 26) ou superior. Para testar a câmera, conceda a permissão solicitada na primeira abertura da tela de captura.

O módulo usa `compileSdk 35`, `targetSdk 35`, Java 17, Kotlin 2.0.21, Android Gradle Plugin 8.7.3 e Jetpack Compose Material 3. As dependências CameraX e ML Kit estão declaradas no `app/build.gradle.kts`.

O DataStore é local e não requer servidor. Quando o aplicativo é instalado pela primeira vez, a ausência de dados gravados faz o repositório mostrar os três registros de exemplo. Depois da primeira operação de gravação, os dados passam a ser lidos do armazenamento local.

## Observações para apresentação

A câmera é uma extensão prática do cadastro: o valor detectado aparece como Toast para demonstrar a análise de imagem. A integração não preenche automaticamente um registro porque o formato de código de cada fornecedor pode variar; a lógica pode ser estendida com uma tabela de códigos ou com uma API.

O `Slider` de filtro de estoque baixo funciona como um estado booleano graduado: qualquer valor maior que zero ativa a regra `quantidade <= estoque mínimo`. No formulário, o mesmo componente representa diretamente o limite de unidades.

## Referências

[1]: https://developer.android.com/develop/ui/compose "Jetpack Compose — Android Developers"

[2]: https://developer.android.com/topic/libraries/architecture/datastore "DataStore — Android Developers"

[3]: https://developer.android.com/training/camerax "CameraX — Android Developers"

[4]: https://developers.google.com/ml-kit/vision/barcode-scanning/android "ML Kit Barcode Scanning — Google Developers"
