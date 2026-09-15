<<<<<<< HEAD
# KitBox
APP Android para Controle de estoque de componentes eletrônicos construído. (MVVM + Jetpack Compose)
=======
# Kit Box 🧰

**Kit Box** é uma aplicação mobile desenvolvida em Kotlin e Jetpack Compose para simplificar e organizar o gerenciamento de inventário de componentes eletrônicos (como resistores, capacitores, sensores e microcontroladores).

A ferramenta foi projetada com foco em usabilidade, performance e clareza visual, permitindo que estudantes, hobistas e engenheiros monitorem rapidamente o volume de peças disponíveis em seu estoque físico.


---

## ✨ Principais Recursos

- **Gerenciamento de Estoque (CRUD):** Cadastro completo, consulta detalhada, edição e remoção de componentes eletrônicos.
- **Alertas Visuais de Reposição:** Barra de progresso visual que altera dinamicamente sua cor (verde para estoque regular e vermelho com tag *"Repor em breve"* quando atinge o limite mínimo definido)[cite: 3].
- **Filtros e Busca Inteligente:** Pesquisa em tempo real por nome ou localização física, além de chips para filtragem rápida por favoritos e categorias (Resistor, Sensor, Microcontrolador, Capacitor, Outro)[cite: 3].
- **Organização Física:** Registro da localização exata do item em organizadores/gaveteiros (ex: *"Gaveta 1"*, *"Gaveta 4"*)[cite: 3].
- **Interface Reativa:** Suporte a feedback visual em formulários (*Sliders*, *Switches*), modais de confirmação de exclusão para evitar perda de dados e tratamento do estado de lista vazia (*Empty State*)[cite: 3].
- **Persistência Local:** Salvamento seguro do inventário via **DataStore** e **Gson**[cite: 3].

---

## 📋 Requisitos Demonstrados

- **CRUD Completo:** Criação, leitura, atualização e exclusão de itens de estoque[cite: 3].
- **Gerenciamento de Estado Reativo:** Interface reativa construída totalmente com Jetpack Compose[cite: 3].
- **Persistência de Dados Local:** Armazenamento contínuo dos dados do app com DataStore/Gson[cite: 3].
- **Navegação Declarativa:** Navegação estruturada entre telas (*MainScreen*, *FormScreen*, *DetailScreen*)[cite: 3].
- **Componentes Customizados & UX:** Uso de componentes visuais do Material 3 com validações de entrada e alertas de estoque[cite: 3].

---

## 📂 Estrutura do Projeto

```text
com.example.kitbox/
│
├── data/                  # Gerenciamento de persistência local (DataStore e Gson)
├── model/                 # Modelos de dados (Componente, Categorias)
├── ui/
│   ├── components/        # Componentes visuais reutilizáveis (Cards, Inputs, Dialogs)
│   ├── navigation/        # Grafo de navegação do aplicativo
│   ├── screens/           # Telas (MainScreen, FormScreen, DetailScreen)
│   └── theme/             # Tema visual, cores e tipografia
└── viewmodel/             # ViewModels para controle de estado da UI
>>>>>>> 38f4629ad1247d087b56c9bbba1295f383659611
