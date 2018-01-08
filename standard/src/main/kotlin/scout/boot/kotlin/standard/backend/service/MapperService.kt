package scout.boot.kotlin.standard.backend.service

import org.modelmapper.ModelMapper

interface MapperService<MODEL, ENTITY> {

    val mapper: ModelMapper
        get() = ModelMapper()

    fun convertToModel(text: ENTITY, clazz: Class<MODEL>): MODEL = mapper.map(text, clazz) as MODEL

    fun convertToEntity(text: MODEL, clazz: Class<ENTITY>): ENTITY = mapper.map(text, clazz) as ENTITY

}
