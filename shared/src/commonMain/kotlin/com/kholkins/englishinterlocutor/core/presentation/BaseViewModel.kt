package com.kholkins.englishinterlocutor.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholkins.englishinterlocutor.core.domain.FlowUseCase
import com.kholkins.englishinterlocutor.core.domain.FlowUseCaseWithoutParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected fun <P, T> runFlowUseCase(
        useCase: FlowUseCase<P, T>,
        params: P,
        showError: Boolean = true,
        onEach: (T) -> Unit,
        onError: (Throwable) -> Unit,
        doFinally: () -> Unit = {},
        coroutineScope: CoroutineScope = viewModelScope
    ): Job {
        return coroutineScope.launch {
            useCase(params)
                .catch { throwable ->
                    onError(throwable)
                }
                .onCompletion {
                    doFinally()
                }
                .collect { value ->
                    onEach(value)
                }
        }
    }

    protected fun <T> runFlowUseCase(
        useCase: FlowUseCaseWithoutParams<T>,
        showError: Boolean = true,
        onEach: (T) -> Unit,
        onError: (Throwable) -> Unit,
        doFinally: () -> Unit = {},
        coroutineScope: CoroutineScope = viewModelScope
    ): Job {
        return coroutineScope.launch {
            useCase()
                .catch { throwable ->
                    onError(throwable)
                }
                .onCompletion {
                    doFinally()
                }
                .collect { value ->
                    onEach(value)
                }
        }
    }
}