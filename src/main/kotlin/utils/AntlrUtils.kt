package utils

import stellaParser

val stellaParser.DeclFunContext.functionName: String
    get() = this.name.text


val stellaParser.DeclFunGenericContext.functionName: String
    get() = this.name.text


val stellaParser.ParamDeclContext.paramName: String
    get() = this.name.text
