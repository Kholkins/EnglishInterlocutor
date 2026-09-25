package com.kholkins.englishinterlocutor.core.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, out R>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IFlowUseCase<P, R> {

    protected abstract suspend fun execute(params: P): Flow<R>

    final override suspend operator fun invoke(params: P): Flow<R> {

        return execute(params).flowOn(dispatcher)
    }
}

// Версия без параметров
abstract class FlowUseCaseWithoutParams<out R>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IFlowUseCaseWithoutParams<R> {

    protected abstract suspend fun execute(): Flow<R>

    final override suspend operator fun invoke(): Flow<R> {

        return execute().flowOn(dispatcher)
    }
}