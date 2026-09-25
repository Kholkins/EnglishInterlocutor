package com.kholkins.englishinterlocutor.core.domain

import kotlinx.coroutines.flow.Flow

// Базовый интерфейс для всех юзкейсов с параметрами
interface UseCase<in P, out R> {
    operator fun invoke(params: P): R
}

// Базовый интерфейс для юзкейсов без параметров
interface UseCaseWithoutParams<out R> {
    operator fun invoke(): R
}

// Базовый интерфейс для потоковых юзкейсов с параметрами
interface IFlowUseCase<in P, out R> {
    suspend operator fun invoke(params: P): Flow<R>
}

// Базовый интерфейс для потоковых юзкейсов без параметров
interface IFlowUseCaseWithoutParams<out R> {
    suspend operator fun invoke(): Flow<R>
}

// Базовый интерфейс для потоковых юзкейсов с параметрами
interface ICoroutinesUseCase<in P, out R> {
    suspend operator fun invoke(params: P): R
}

// Базовый интерфейс для потоковых юзкейсов без параметров
interface ICoroutinesUseCaseWithoutParams<out R> {
    suspend operator fun invoke(): R
}