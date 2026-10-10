# 🎙️ EnglishInterlocutor

**Kotlin Multiplatform приложение для практики английского языка с голосовым AI-собеседником**

Приложение позволяет практиковать английский язык через голосовой диалог с AI. Пользователь говорит фразу на английском — приложение мгновенно переводит её на русский, а AI-учитель отвечает контекстной фразой для продолжения разговора.

## ✨ Функционал

- 🎤 **Голосовой ввод** — удерживайте кнопку для разговора (Speech Recognition на Android / SFSpeechRecognizer на iOS)
- 🌍 **Мгновенный перевод** — английский текст переводится на русский через Yandex Translate API
- 🤖 **AI-собеседник** — YandexGPT генерирует ответные фразы с контекстной поддержкой диалога
- 📱 **Двуязычный интерфейс** — сообщения отображаются на английском с скрытым переводом (механика "квиза")
- 🔄 **Кроссплатформенность** — общий код для Android и iOS через Kotlin Multiplatform

## 🛠 Технологии

| Категория | Технология |
|---|---|
| **Язык** | Kotlin 2.4.10 |
| **UI** | Compose Multiplatform 1.12.0 |
| **Архитектура** | Kotlin Multiplatform (KMP) |
| **DI** | Koin 4.0.2 |
| **Сеть** | Ktor Client 3.5.2 |
| **Корутины** | kotlinx-coroutines 1.10.2 |
| **Сериализация** | kotlinx-serialization 1.9.0 |
| **Бэкенд** | Yandex Cloud (Yandex Translate + YandexGPT) |

## 📐 Архитектура

Проект следует **Clean Architecture** с чётким разделением слоёв:

```
shared/
├── domain/              ← Чистая бизнес-логика (без зависимостей)
│   ├── model/           ← Sealed interfaces: AiState, TranslateState, SpeechRecognitionEvent
│   ├── repository/      ← Интерфейсы репозиториев
│   └── usecase/         ← UseCase классы (Translate, AI Dialogue, Speech Recognition)
├── data/                ← Реализации репозиториев и API
│   ├── api/             ← BackendApi (Ktor HTTP client)
│   └── network/         ← HttpClientFactory (expect/actual)
├── presentation/        ← UI и ViewModel
│   ├── speech/          ← SpeechScreen (Compose), SpeechViewModel
│   └── navigation/      ← AppNavigation
├── di/                  ← Koin модули
└── core/                ← Базовые классы (BaseViewModel, FlowUseCase)
```

### Кроссплатформенность

- **commonMain** — общая бизнес-логика, домен, UI (Compose Multiplatform)
- **androidMain** — OkHttp, SpeechRecognizer, Android-специфичные реализации
- **iosMain** — Darwin (NSURLSession), SFSpeechRecognizer, iOS-специфичные реализации

Используется паттерн `expect/actual` для платформенно-зависимого кода.

## 📱 Скриншоты

![Speech Screen](docs/screenshots/speech-screen.png)

> Интерфейс: удерживайте кнопку для разговора, нажмите на перевод для проверки

## 🚀 Быстрый старт

### Требования

- Android Studio Hedgehog или новее
- JDK 17+
- Для iOS: macOS с Xcode 15+

### Запуск Android

```bash
./gradlew :androidApp:assembleDebug
```

Или через Android Studio: запустите конфигурацию `androidApp`.

### Запуск iOS

```bash
cd iosApp
open iosApp.xcodeproj
```

Откройте `iosApp/iosApp.xcodeproj` в Xcode и запустите.

### Запуск тестов

```bash
# Android unit tests
./gradlew :shared:testAndroidHostTest

# iOS unit tests (требуется macOS)
./gradlew :shared:iosSimulatorArm64Test
```

## 🔌 API

Приложение подключается к Yandex Cloud:

| Endpoint | Описание |
|---|---|
| `/api/translate` | Перевод EN → RU (Yandex Translate API) |
| `/api/gpt/chat` | AI-ответы (YandexGPT) |
| `/api/health` | Проверка доступности |

Настройка URL бэкенда через `gradle.properties`:

```properties
BACKEND_URL=https://your-backend.yandexcloud.net
```

## 🧪 Тесты

Проект включает **44 unit-теста** для покрытия бизнес-логики:

- **Domain models** — проверка sealed interfaces и data class свойств
- **UseCase** — тесты для Translate, AI Dialogue, Speech Recognition
- **ViewModel** — тесты управления состоянием и обработки событий

```bash
./gradlew :shared:testAndroidHostTest
```

## 📂 Структура проекта

```
EnglishInterlocutor/
├── androidApp/            # Android entry point (Compose)
├── iosApp/                # iOS entry point (SwiftUI + Compose)
├── shared/                # KMP модуль (общий код)
│   ├── src/
│   │   ├── commonMain/    # Общая логика
│   │   ├── androidMain/   # Android-специфичный код
│   │   └── iosMain/       # iOS-специфичный код
│   └── build.gradle.kts
├── gradle/                # Gradle wrapper и версии
└── settings.gradle.kts
```

## 🤝 Вклад

Вклад приветствуется! Для отправки PR:

1. Fork репозитория
2. Создайте feature-ветку
3. Добавьте тесты для новых функций
4. Убедитесь, что все тесты проходят: `./gradlew :shared:testAndroidHostTest`
5. Отправьте Pull Request

## 📄 Лицензия

MIT License

## 🔗 Ресурсы

- [Kotlin Multiplatform Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/)
- [Koin DI](https://insert-koin.io/)
- [Ktor Client](https://ktor.io/docs/client.html)
