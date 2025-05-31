package com.example.rumatiabutbattletanks.enums

enum class Material(
    val tankCanGoThrough: Boolean,
    val bulletCanGoThrough: Boolean,
    val simpleBulletCanDestroy: Boolean
) {
    EMPTY(true, bulletCanGoThrough = true, simpleBulletCanDestroy = true),
    BRICK(false, bulletCanGoThrough = false, simpleBulletCanDestroy = true),
    CONCRETE(false, bulletCanGoThrough = false, simpleBulletCanDestroy = false),
    GRASS(true, bulletCanGoThrough = true, simpleBulletCanDestroy = false),
    EAGLE(tankCanGoThrough = false, bulletCanGoThrough = false, simpleBulletCanDestroy = true)
}