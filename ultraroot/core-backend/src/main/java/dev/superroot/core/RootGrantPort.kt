package dev.superroot.core

/** Escrita da allowlist. Sem implementação nativa nesta fase: só fakes. */
interface RootGrantPort {
    fun grant(uid: Int): KsuRead<Unit>
    fun revoke(uid: Int): KsuRead<Unit>
}
