package com.luxoft.blockchainlab.corda.hyperledger.indy.data.state

import com.luxoft.blockchainlab.corda.hyperledger.indy.data.schema.ClaimDefinitionSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState


/**
 * A Corda record of an indy credential definition.
 *
 * @param schemaId              id of schema coupled with this credential definition
 * @param claimDefId            id of this credential definition
 * @param revRegId              id of revocation registry coupled with this credential definition
 * @param credentialsLimit      maximum number of credential which can be issued using this credential definition
 * @param participants          corda participants
 * @param currentCredNumber     current number of credentials issued using this credential definition
 */
data class IndyClaimDefinition(
    val schemaId: String,
    val claimDefId: String,
    val revRegId: String,
    val credentialsLimit: Int,
    override val participants: List<AbstractParty>,
    val currentCredNumber: Int = 0
) : LinearState, QueryableState {

    override val linearId: UniqueIdentifier = UniqueIdentifier()

    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is ClaimDefinitionSchemaV1 -> ClaimDefinitionSchemaV1.PersistentClaimDefinition(this)
            else -> throw IllegalArgumentException("Unrecognised schema: $schema")
        }
    }

    override fun supportedSchemas() = listOf(ClaimDefinitionSchemaV1)

    /**
     * Returns true if this credential definition is able to hold 1 more credential
     */
    fun canProduceCredentials() = currentCredNumber < credentialsLimit

    fun requestNewCredential() = copy(currentCredNumber = this.currentCredNumber + 1)
}