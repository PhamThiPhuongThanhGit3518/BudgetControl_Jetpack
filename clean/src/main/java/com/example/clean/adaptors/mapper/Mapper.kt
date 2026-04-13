package com.example.clean.adaptors.mapper

interface Mapper<Local, Domain> {
    fun toDomain(local: Local): Domain
    fun toLocal(domain: Domain): Local
}