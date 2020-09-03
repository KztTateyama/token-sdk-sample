package sbi.contracts

import com.r3.corda.lib.tokens.contracts.states.FungibleToken
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import sbi.states.GSCoinIssuanceRecord
import sbi.states.GSCoinMoveRecord

class GSCoinRecordContract : Contract
{
    companion object
    {
        const val ID = "sbi.contracts.GSCoinRecordContract"
    }

    interface Commands : CommandData {
        class Issue : TypeOnlyCommandData(), Commands
        class Move : TypeOnlyCommandData(), Commands
    }

     override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()
        when (command.value) {
            is Commands.Issue -> requireThat {
                // Verify input and output counts
                "Input should be empty." using (tx.inputs.isEmpty())
                "There should be 2 output states." using (tx.outputs.size == 2)

                // Verify transaction contents
                val record = tx.outputsOfType<GSCoinIssuanceRecord>().single()
                "The amount in GSCoinIssuanceRecord must be positive" using (record.amount.quantity > 0)
                "tradeNumber in GSCoinIssuanceRecord must not be null or blank" using (!record.tradeNumber.isNullOrBlank())
                "tradeDateTime in GSCoinIssuanceRecord must not be null or blank" using (!record.tradeDateTime.isNullOrBlank())

                val token = tx.outputsOfType<FungibleToken>().single()
                "Holders in GSCoinIssuanceRecord and FungibleToken must be the same identity" using (token.holder == record.holder)

                // Verify signatures
                "All participants must be signers." using (
                        command.signers.toSet().containsAll(record.participants.map { it.owningKey }))

            }
            is Commands.Move -> requireThat {
                // Verify input and output counts
                "There should be at least one FungibleToken state in input." using (!tx.inputsOfType<FungibleToken>().isEmpty())
                "There should be at lease one FungibleToken state in output" using (!tx.outputsOfType<FungibleToken>().isEmpty())
                "There should be one GSCoinMoveRecord in output" using (tx.outputsOfType<GSCoinMoveRecord>().size == 1)

                // Verify transaction contents
                val record = tx.outputsOfType<GSCoinMoveRecord>().single()
                "The amount in GSCoinMoveRecord must be positive" using (record.amount.quantity > 0)
                "The sender and receiver cannot be the same identity" using (record.sender.owningKey != record.receiver.owningKey)
                "tradeNumber in GSCoinMoveRecord must not be null or blank" using (!record.tradeNumber.isNullOrBlank())
                "tradeDateTime in GSCoinMoveRecord must not be null or blank" using (!record.tradeDateTime.isNullOrBlank())
                "memberShopName in GSCoinMoveRecord must not be null or blank" using (!record.memberShopName.isNullOrBlank())

                // Verify signatures
                "All participants must be signers" using (command.signers.toSet().containsAll(record.participants.map { it.owningKey }))
            }
        }

    }
}