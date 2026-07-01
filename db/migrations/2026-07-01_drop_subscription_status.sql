-- Removes the subscription status axis entirely.
--
-- Context: a subscription has a single axis, its plan (FREE / BASIC / PROFESSIONAL). There is no
-- ACTIVE/SUSPENDED lifecycle: a failed payment is left to Stripe's dunning retries, and if Stripe
-- ultimately cancels the subscription the org is downgraded to FREE. Nothing in the domain reads a
-- status anymore, so both the column and its enum type are dropped.
--
-- Run in the Supabase SQL editor. Supersedes 2026-07-01_drop_cancelled_subscription_status.sql
-- (that one only shrank the enum; this removes it outright and is safe to run whether or not it ran).

BEGIN;

ALTER TABLE subscriptions DROP COLUMN IF EXISTS status;

DROP TYPE IF EXISTS subscription_status;

COMMIT;
